from pathlib import Path
from chatbot.services.embedding_service import embed
from chatbot.db.chatbot_repository import save_document
from chatbot.services.chunking_service import split_into_chunks

def ingest_rules_file(game_code: str, path: str):
    text = Path(path).read_text(encoding="utf-8")
    ingest_rules_text(game_code, text)

def ingest_rules_text(game_code: str, text: str):
    chunks = split_into_chunks(text)
    for chunk in chunks:
        vec = embed(chunk)
        save_document(game_code, chunk, vec)
