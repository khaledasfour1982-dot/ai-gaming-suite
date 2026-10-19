from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from chatbot.routers.admin_router import router as admin_router
from chatbot.routers.chat_router import router as chat_router

import os
from dotenv import load_dotenv

load_dotenv()

app = FastAPI(title="BanditGames Chatbot")

origins = [
    "http://localhost:3000",
    "http://127.0.0.1:3000",

]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,      # or ["*"] while developing
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(chat_router, prefix="/api")
app.include_router(admin_router, prefix="/api")
