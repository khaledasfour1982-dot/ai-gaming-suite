FROM ankane/pgvector:latest as schema_init
COPY chatbot/db/schemas.sql /docker-entrypoint-initdb.d/chatbot-schema.sql

FROM python:3.11-slim
WORKDIR /app

COPY requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

COPY . /app

COPY --from=schema_init /docker-entrypoint-initdb.d/ /app/schemas/

CMD ["uvicorn", "chatbot.main:app", "--host", "0.0.0.0", "--port", "8003"]
