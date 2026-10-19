FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir -r /app/requirements.txt

# copy ai code + game_engine module (required for board logic)
COPY ai /app/ai
COPY game_engine /app/game_engine

EXPOSE 8002
CMD ["uvicorn", "ai.main:app", "--host", "0.0.0.0", "--port", "8002"]
