from copy import deepcopy
from typing import Optional

from game_engine.domain.models import GameState
from game_engine.domain.move import Move
from game_engine.domain.rules import (
    compute_legal_moves,
    compute_next_player,
    detect_winner,
    detect_draw,
)


class TicTacToeStateWrapper:
    """
    Wrapper used by MCTS to simulate moves using the shared game engine.
    """

    def __init__(
        self,
        board,
        current_player,
        root_player,
        last_move: Optional[Move],
        config,
        legal_moves,
    ):
        self.board = board
        self.current_player = current_player
        self.root_player = root_player
        self.lastMove = last_move
        self.config = config
        self.legal_moves = legal_moves

    # --------------------------------------------------------
    @classmethod
    def from_game_state(cls, state: GameState):
        board = deepcopy(state.board)
        empty = state.config.empty_mark

        # Normalize empty cells
        board = [
            [empty if cell in ("", "EMPTY", ".", None) else cell for cell in row]
            for row in board
        ]

        legal_moves = compute_legal_moves(
            GameState(board=board, current_player=state.current_player, config=state.config)
        )

        return cls(
            board=board,
            current_player=state.current_player,
            root_player=state.current_player,  # Root player fixed per search
            last_move=None,
            config=state.config,
            legal_moves=[Move(m.row, m.col) for m in legal_moves],
        )

    # --------------------------------------------------------
    def getPossibleActions(self):
        return self.legal_moves

    def getCurrentPlayer(self):
        return self.current_player

    # --------------------------------------------------------
    def isTerminal(self):
        return detect_winner(self.board, self.config) is not None or detect_draw(self.board, self.config)

    # --------------------------------------------------------
    def getReward(self):
        winner = detect_winner(self.board, self.config)
        if winner is None:
            return 0
        return 1 if winner == self.root_player else -1

    # --------------------------------------------------------
    def takeAction(self, move: Move):
        new_board = deepcopy(self.board)
        new_board[move.row][move.col] = self.current_player

        next_player = compute_next_player(self.current_player, self.config)

        new_state = GameState(
            board=new_board,
            current_player=next_player,
            config=self.config,
        )

        new_legal = compute_legal_moves(new_state)

        return TicTacToeStateWrapper(
            board=new_board,
            current_player=next_player,
            root_player=self.root_player,
            last_move=move,
            config=self.config,
            legal_moves=[Move(m.row, m.col) for m in new_legal],
        )
