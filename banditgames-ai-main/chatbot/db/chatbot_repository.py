import psycopg2
from psycopg2.extras import RealDictCursor
import os
from chatbot.services.embedding_service import embed

def get_connection():
    return psycopg2.connect(
        dbname=os.getenv("POSTGRES_DB", "chatbotdb"),
        user=os.getenv("POSTGRES_USER", "postgres"),
        password=os.getenv("POSTGRES_PASSWORD", "postgres"),
        host=os.getenv("POSTGRES_HOST", "postgres_chat"),
        port=int(os.getenv("POSTGRES_PORT", "5432")),
    )

def get_rules_by_game_code(game_code: str):
    with get_connection() as conn, conn.cursor(cursor_factory=RealDictCursor) as cur:
        cur.execute("""
            SELECT gr.section, gr.content
            FROM kdg_chatbot.game_rules gr
            JOIN kdg_chatbot.games g ON gr.game_id = g.id
            WHERE g.code = %s
            ORDER BY gr.id;
        """, (game_code,))
        return cur.fetchall()

def save_document(game_code: str, content: str, embedding: list[float]) -> None:
    with get_connection() as conn:
        with conn.cursor() as cur:
            cur.execute(
                """
                INSERT INTO kdg_chatbot.documents (game_code, content, embedding)
                VALUES (%s, %s, %s::vector)
                """,
                (game_code, content, embedding),
            )

def search_documents_any_game(
    query_vec: list[float],
    limit: int = 5,
):
    with get_connection() as conn, conn.cursor(cursor_factory=RealDictCursor) as cur:
        cur.execute(
            """
            SELECT game_code,
                   content,
                   1 - (embedding <=> %s::vector) AS score
            FROM kdg_chatbot.documents
            ORDER BY embedding <=> %s::vector
            LIMIT %s;
            """,
            (query_vec, query_vec, limit),
        )
        return cur.fetchall()

def list_faqs():
    with get_connection() as conn, conn.cursor(cursor_factory=RealDictCursor) as cur:
        cur.execute("""
            SELECT id, question, answer
            FROM kdg_chatbot.platform_faqs
            ORDER BY id;
        """)
        return cur.fetchall()

def create_faq(question: str, answer: str):
    embedding = embed(question)
    with get_connection() as conn, conn.cursor(cursor_factory=RealDictCursor) as cur:
        cur.execute("""
            INSERT INTO kdg_chatbot.platform_faqs (question, answer, embedding)
            VALUES (%s, %s, %s::vector)
            RETURNING id, question, answer;
        """, (question, answer, embedding))
        return cur.fetchone()

def delete_faq(faq_id: int) -> None:
    with get_connection() as conn, conn.cursor() as cur:
        cur.execute(
            "DELETE FROM kdg_chatbot.platform_faqs WHERE id = %s;",
            (faq_id,),
        )

def search_platform_faqs_any(query_vec, limit: int = 1) -> list[dict]:
    with get_connection() as conn, conn.cursor() as cur:
        cur.execute("""
            SELECT id, question, answer, 1 - (embedding <=> %s::vector) AS score
            FROM kdg_chatbot.platform_faqs
            WHERE embedding IS NOT NULL
            ORDER BY embedding <=> %s::vector
            LIMIT %s
        """, (query_vec, query_vec, limit))
        return [
            {"id": r[0], "question": r[1], "answer": r[2], "score": r[3]}
            for r in cur.fetchall()
        ]

