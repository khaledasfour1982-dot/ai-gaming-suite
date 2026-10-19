from chatbot.db.chatbot_repository import get_connection
from chatbot.services.ingestion_service import ingest_rules_file

if __name__ == "__main__":
    conn = get_connection()
    print("Ingest using:", conn.dsn)
    conn.close()
    ingest_rules_file("CHESS", "chatbot/data/chess_rules.txt")
    print("Ingested chess rules.")
