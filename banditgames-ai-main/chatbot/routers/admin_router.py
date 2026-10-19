from fastapi import APIRouter, UploadFile, File
from chatbot.services.ingestion_service import ingest_rules_text
from pydantic import BaseModel
from chatbot.db.chatbot_repository import list_faqs, create_faq, delete_faq

router = APIRouter(prefix="/admin", tags=["admin"])

@router.post("/rules/{game_code}")
async def upload_rules(game_code: str, file: UploadFile = File(...)):
    raw = await file.read()
    text = raw.decode("utf-8")
    ingest_rules_text(game_code.upper(), text)
    return {"status": "ok"}

class FaqCreate(BaseModel):
    question: str
    answer: str

@router.get("/platform-faqs")
def get_platform_faqs():
    return list_faqs()

@router.post("/platform-faqs")
def add_platform_faq(payload: FaqCreate):
    return create_faq(payload.question, payload.answer)

@router.delete("/platform-faqs/{faq_id}", status_code=204)
def remove_platform_faq(faq_id: int):
    delete_faq(faq_id)
    return