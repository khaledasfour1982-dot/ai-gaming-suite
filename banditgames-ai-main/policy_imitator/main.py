from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import uvicorn

from policy_imitator.policy_router import router as policy_router

app = FastAPI(
    title="ML Policy Service",
    version="1.0.0",
    description="Policy model inference service for Tic-Tac-Toe"
)

origins = [
    "http://localhost:5174",
    "http://localhost:3000",
    "http://localhost:3001",
    "http://localhost:5173",
    "http://127.0.0.1:5174",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Register routers
app.include_router(policy_router)

@app.get("/health")
def health():
    return {"status": "ok"}

if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host="localhost",
        port=8000,
        reload=True
    )
