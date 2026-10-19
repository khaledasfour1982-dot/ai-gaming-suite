import functools
from typing import Tuple

from fastapi import APIRouter

from chatbot.db.chatbot_repository import (
    get_rules_by_game_code,
    search_platform_faqs_any,
    search_documents_any_game,
)
from chatbot.services.embedding_service import embed
from chatbot.services.llm_services import answer_with_context

router = APIRouter(tags=["chat"])


# Rules helpers
def get_tictactoe_rules_text() -> str:
    return get_cached_rules("TIC_TAC_TOE")


def get_chess_rules_text() -> str:
    return get_cached_rules("CHESS")


@functools.lru_cache(maxsize=128)
def get_cached_rules(game_code: str) -> str:
    rows = get_rules_by_game_code(game_code)
    return (
        "\n".join(f"{r['section']}: {r['content']}" for r in rows)
        if rows
        else ""
    )


def get_platform_answer(message: str) -> str | None:
    msg = message.strip().lower()

    platform_keywords = ["platform", "stats", "statistics", "friend", "invite",
                         "account", "profile", "login", "register"]
    if len(msg) < 10 or not any(k in msg for k in platform_keywords):
        return None

    # 2) Vector search
    q_vec = embed(msg)
    if not q_vec:
        return None

    docs = search_platform_faqs_any(q_vec, limit=1)
    if not docs:
        return None

    best = docs[0]
    if best["score"] < 0.6:   # high threshold so only strong matches pass
        return None

    return best["answer"]



# Vector-based game QA
def answer_question_any_game(question: str) -> Tuple[str, str | None]:
    """
    Answer a rules question by searching across all games.
    Returns (answer, detected_game_code | None).
    """
    q_vec = embed(question)
    if not q_vec:
        return (
            "I couldn’t understand the question well enough to answer.",
            None,
        )

    # Search across all games, not filtered by game_code
    docs = search_documents_any_game(q_vec, limit=5)
    if not docs:
        return (
            "I couldn’t find any rules for that in the knowledge base.",
            None,
        )

    # docs like: [{"game_code": "...", "content": "...", "score": ...}, ...]
    best = docs[0]
    best_game = best["game_code"]
    best_score = best.get("score", 1.0)

    # Safety threshold – tune as needed
    if best_score < 0.3:
        return (
            "I couldn’t find any rules for that in the knowledge base.",
            None,
        )

    context = "\n\n".join(d["content"] for d in docs)
    answer = answer_with_context(question, context)

    return answer, best_game
