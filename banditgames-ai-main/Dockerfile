FROM python:3.11-slim

WORKDIR /app
COPY . /app

RUN pip install fastapi uvicorn pydantic

EXPOSE 8001

CMD ["uvicorn", "game_engine_main:app", "--host", "0.0.0.0", "--port", "8001"]
