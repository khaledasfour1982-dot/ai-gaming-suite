import os
import requests

GROQ_URL = "https://api.groq.com/openai/v1/chat/completions"
CANDIDATE_MODELS = [
    "llama-3.1-8b-instant"
]


def _call_groq(model: str, prompt: str, max_new_tokens: int = 256, temperature: float = 0.7) -> str:
    key = os.environ.get("GROQ_API_KEY")
    if not key or key == "gsk_YOUR_KEY_HERE":
        raise RuntimeError("No GROQ_API_KEY set.")
    r = requests.post(
        GROQ_URL,
        headers={"Authorization": f"Bearer {key}", "Content-Type": "application/json"},
        json={
            "model": model,
            "messages": [
                {"role": "system", "content": "You are a helpful BanditGames assistant."},
                {"role": "user", "content": prompt},
            ],
            "temperature": temperature,
            "max_tokens": max_new_tokens,
            "stream": False,
        },
        timeout=120,
    )
    if r.status_code >= 400:
        raise RuntimeError(f"{r.status_code}: {r.text[:300]}")
    return r.json()["choices"][0]["message"]["content"].strip()

def ask_groq(prompt: str, max_new_tokens: int = 256, temperature: float = 0.7) -> str:
    last_err = None
    for model in CANDIDATE_MODELS:
        try:
            return _call_groq(model, prompt, max_new_tokens, temperature)
        except RuntimeError as e:
            msg = str(e)
            if "model_decommissioned" in msg or "invalid_request_error" in msg or "model_not_found" in msg:
                last_err = e
                continue
            raise
    raise last_err if last_err else RuntimeError("None of Groq models is accessible.")


def answer_with_context(question: str, context: str, conversation_history: str = "",
                        is_first_message: bool = False) -> str:
    greeting = "Hi! " if is_first_message else ""

    prompt = f"""You are BANDIT, BanditGames' professional customer support agent. Friendly, precise, knowledgeable about board games and platform features.

TASK: Answer user questions about BanditGames using ONLY the provided CONTEXT.

REQUIREMENTS:
1. Read CONTEXT completely (your ONLY knowledge source)
2. Answer using facts from CONTEXT only
3. Be concise (2-4 sentences max), natural, helpful
4. {'START with greeting' if is_first_message else 'NO greeting - continue conversation naturally'}
5. If insufficient info: "Sorry, I don't have that detail. Ask about Tic Tac Toe/Chess rules or platform basics!"

EXAMPLES:

**FIRST MESSAGE:**
Q: "How do I start Tic Tac Toe?"  
CONTEXT: "Go to game list, select Tic Tac Toe, create/join lobby"  
A: "Hi! Go to the game list, select Tic Tac Toe, and create or join a lobby. Need help with rules or other games?"

**FOLLOW-UP:**
Q: "ok then how start game"  
CONTEXT: "Create lobby → Invite friends → Start game"  
A: "Create or join a lobby from the game list, then click Start Game. Ready to play?"

**ANOTHER FIRST MESSAGE:**
Q: "Chess winning conditions?"  
CONTEXT: "Checkmate ends the game"  
A: "Hi! Chess ends when a player checkmates the opponent's king. Want the full rules?"

CONTEXT:
{context}

CONVERSATION HISTORY:
{conversation_history}

USER QUESTION: {question}

FORMAT:
BANDIT: {greeting}[Answer using CONTEXT facts]. Need help with rules or platform features?"""

    return ask_groq(prompt, max_new_tokens=250, temperature=0.15)
