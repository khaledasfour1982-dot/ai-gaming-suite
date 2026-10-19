from fastapi import APIRouter
from chatbot.models.schemas import ChatRequest, ChatResponse
from chatbot.services.hardcoded_faq import get_hardcoded_answer
from chatbot.services.retrieval_service import (
    get_platform_answer,
    answer_question_any_game,
)
from chatbot.services.llm_services import answer_with_context
from chatbot.services.cache_service import get_cached_answer, set_cached_answer
from chatbot.services.chat_manager import set_last_game
import logging

router = APIRouter(tags=["chat"])
logger = logging.getLogger("uvicorn.error")

@router.post("/chat", response_model=ChatResponse)
def chat(req: ChatRequest):
    msg = req.message.lower()
    session_id = req.session_id

    has_seen = get_cached_answer(session_id, "__seen__")
    is_first = has_seen is None

    # Platform FAQs (no game context)
    platform_raw = get_platform_answer(msg)
    if platform_raw is not None:
        cached = get_cached_answer(None, req.message)
        if cached:
            return ChatResponse(answer=cached)

        answer = answer_with_context(
            req.message,
            platform_raw,
            is_first_message=is_first,
        )
        set_cached_answer(None, req.message, answer)
        set_cached_answer(session_id, "__seen__", "1")
        return ChatResponse(answer=answer)

    # Hardcoded answers (for quick answer)
    hard = get_hardcoded_answer(None, req.message)
    if hard is not None:
        if is_first:
            hard = "Hi! " + hard.lstrip()
        set_cached_answer(session_id, "__seen__", "1")
        return ChatResponse(answer=hard)

    # Cache by question only (since we search all games)
    cached = get_cached_answer(None, req.message)
    if cached:
        logger.info(f"CACHE HIT q={req.message!r}")
        return ChatResponse(answer=cached)

    logger.info(f"CACHE MISS q={req.message!r}")

    # Vector routing across all games
    answer, detected_game = answer_question_any_game(req.message)

    if detected_game:
        logger.info(f"DETECTED_GAME game={detected_game} q={req.message!r}")
        set_last_game(session_id, detected_game)

    if is_first:
        answer = "Hi! " + answer.lstrip()
    set_cached_answer(session_id, "__seen__", "1")

    set_cached_answer(None, req.message, answer)
    return ChatResponse(answer=answer)
