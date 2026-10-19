# game_engine_main.py
from fastapi import FastAPI
from game_engine.controller import router as game_router

app = FastAPI(title="TicTacToe Game Engine")

app.include_router(game_router, prefix="/api")
