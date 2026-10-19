FROM python:3.11-slim

WORKDIR /app

# install dependencies
COPY requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir -r /app/requirements.txt

# copy ONLY game_engine code
COPY policy_imitator /app/policy_imitator

EXPOSE 8010
CMD ["uvicorn", "policy_imitator.main:app", "--host", "0.0.0.0", "--port", "8010"]
