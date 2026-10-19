from fastapi import FastAPI
from ai import controller

app = FastAPI(title="AI Player Service")

app.include_router(controller.router, prefix="/api")

