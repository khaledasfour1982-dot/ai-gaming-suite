FROM python:3.12-slim

WORKDIR /app

COPY ML_winning_probability/requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir -r /app/requirements.txt

COPY ML_winning_probability/winprob_api.py /app/winprob_api.py
COPY ML_winning_probability/winprob_best_model.joblib /app/winprob_best_model.joblib

EXPOSE 8004

CMD ["uvicorn", "winprob_api:app", "--host", "0.0.0.0", "--port", "8004"]