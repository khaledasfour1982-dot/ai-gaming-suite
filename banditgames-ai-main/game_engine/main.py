from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from game_engine.api.controller.session_controller import router as session_router
from game_engine.api.controller.game_controller import router as game_router
from game_engine.infrastructure.database import init_database
from game_engine.api.controller.ai_controller import router as ai_router
from game_engine.api.controller.export_controller import router as export_router
from game_engine.infrastructure.messaging.rabbitmq import RabbitMQPublisher
from game_engine.infrastructure.platform_database import platform_db

app = FastAPI(title="TicTacToe Game Engine")

@app.on_event("startup")
async def startup():
    await init_database()
    # Platform database connection is optional - game engine works without it
    await platform_db.connect()
    await RabbitMQPublisher.connect()

@app.on_event("shutdown")
async def shutdown():
    await platform_db.disconnect()

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

app.include_router(session_router)
app.include_router(game_router)

app.include_router(ai_router)
app.include_router(export_router)
