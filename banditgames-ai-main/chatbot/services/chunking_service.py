def split_into_chunks(text: str, max_chars: int = 500) -> list[str]:
    """
    Very simple chunker: splits on double newlines, then merges
    until ~max_chars per chunk.
    """
    raw_parts = [p.strip() for p in text.split("\n\n") if p.strip()]
    chunks: list[str] = []
    current = ""

    for part in raw_parts:
        if len(current) + len(part) + 2 <= max_chars:
            current = f"{current}\n\n{part}" if current else part
        else:
            chunks.append(current)
            current = part

    if current:
        chunks.append(current)

    return chunks
