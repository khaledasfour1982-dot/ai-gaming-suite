

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from ai.controller import router as ai_router


app = FastAPI(title="BanditGames AI ")

origins = [
    "http://localhost:3000",
    "http://127.0.0.1:3000",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(ai_router)
