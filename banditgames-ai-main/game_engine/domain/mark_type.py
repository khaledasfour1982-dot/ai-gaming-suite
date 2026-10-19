from enum import Enum


class MarkType(str, Enum):
    X = "X"
    O = "O"
    G = "G"
    H = "H"

    # Future-proof: you can add custom symbols easily.
    CAT = "🐱"
    DOG = "🐶"
    STAR = "⭐"
    FIRE = "🔥"
    HEART = "❤️"
    SKULL = "💀"
