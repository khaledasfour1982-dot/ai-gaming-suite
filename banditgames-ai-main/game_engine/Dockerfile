FROM python:3.11-slim

WORKDIR /app

# install dependencies
COPY requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir -r /app/requirements.txt

# copy ONLY game_engine code
COPY game_engine /app/game_engine

EXPOSE 8001
CMD ["uvicorn", "game_engine.main:app", "--host", "0.0.0.0", "--port", "8001"]
