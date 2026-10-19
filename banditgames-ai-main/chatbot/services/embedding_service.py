import os
import requests

EMBEDDING_HOST = os.getenv("EMBEDDING_HOST", "host.docker.internal")
EMBEDDING_PORT = int(os.getenv("EMBEDDING_PORT", "11434"))
EMBEDDING_MODEL = os.getenv("EMBEDDING_MODEL", "mxbai-embed-large")

def embed(text: str) -> list[float]:
    r = requests.post(
        f"http://{EMBEDDING_HOST}:{EMBEDDING_PORT}/api/embed",
        json={"model": EMBEDDING_MODEL, "input": text},
        timeout=30,
    )
    r.raise_for_status()
    data = r.json()
    embs = data.get("embeddings") or []
    if not embs:
        # fall back to a dummy vector if Ollama returned nothing
        return [0.0] * 1024
    return embs[0]
