import math
import random
from game_engine.domain.rules import detect_winner


# ============================================================
# MCTS NODE
# ============================================================
class MCTSNode:
    def __init__(self, state, parent=None):
        self.state = state
        self.parent = parent
        self.children = {}        # move → child node
        self.numVisits = 0
        self.totalReward = 0.0

    def is_fully_expanded(self):
        return len(self.children) == len(self.state.getPossibleActions())

    def best_child(self, c):
        best_score = -1e9
        best_child = None

        for move, child in self.children.items():
            exploit = child.totalReward / child.numVisits if child.numVisits > 0 else 0
            explore = math.sqrt(math.log(self.numVisits + 1) / (child.numVisits + 1e-9))
            score = exploit + c * explore

            if score > best_score:
                best_score = score
                best_child = child

        return best_child


# ============================================================
# HELPER FUNCTIONS - SIMPLE AND WORKING
# ============================================================

def can_win_next_move(board, player, config):
    """
    Check if player can win with ONE more move.
    Returns the Move object if yes, None otherwise.
    """
    size = config.size
    empty = config.empty_mark

    # Check all empty positions
    for row in range(size):
        for col in range(size):
            if board[row][col] == empty:
                # Try this move
                test_board = [r[:] for r in board]
                test_board[row][col] = player

                # Check if this wins
                if detect_winner(test_board, config) == player:
                    from game_engine.domain.move import Move
                    return Move(row, col)

    return None


def count_winning_moves(board, player, config):
    """
    Count how many different moves would win the game.
    Used for fork detection.
    """
    size = config.size
    empty = config.empty_mark
    winning_count = 0

    for row in range(size):
        for col in range(size):
            if board[row][col] == empty:
                test_board = [r[:] for r in board]
                test_board[row][col] = player
                if detect_winner(test_board, config) == player:
                    winning_count += 1

    return winning_count


def find_fork_move(board, player, config, available_moves):
    """
    Find a move that creates a FORK (2+ ways to win).
    """
    for move in available_moves:
        # Simulate the move
        test_board = [r[:] for r in board]
        test_board[move.row][move.col] = player

        # Count how many winning moves we'd have after this
        winning_moves = count_winning_moves(test_board, player, config)

        # If 2+ winning moves, it's a fork!
        if winning_moves >= 2:
            return move

    return None


# ============================================================
# ROLLOUT POLICIES
# ============================================================

# -------- EASY: RANDOM --------
def rollout_easy(state):
    sim = state
    while not sim.isTerminal():
        moves = sim.getPossibleActions()
        if not moves:
            break
        sim = sim.takeAction(random.choice(moves))
    return sim.getReward()


# -------- MEDIUM: BLOCK-ONLY --------
def rollout_medium(state):
    sim = state

    while not sim.isTerminal():
        moves = sim.getPossibleActions()
        if not moves:
            break

        curr = sim.current_player
        opp = "O" if curr == "X" else "X"

        # Block opponent winning move
        for m in moves:
            ns = sim.takeAction(m)
            if ns.isTerminal() and detect_winner(ns.board, ns.config) == opp:
                sim = sim.takeAction(m)
                break
        else:
            # Otherwise random
            sim = sim.takeAction(random.choice(moves))

    return sim.getReward()


# -------- HARD: WIN OR BLOCK --------
def rollout_hard(state):
    sim = state

    while not sim.isTerminal():
        moves = sim.getPossibleActions()
        if not moves:
            break

        curr = sim.current_player
        opp = "O" if curr == "X" else "X"

        # 1. Win immediately if possible
        for m in moves:
            ns = sim.takeAction(m)
            if ns.isTerminal() and detect_winner(ns.board, ns.config) == curr:
                sim = ns
                break
        else:
            # 2. Block opponent immediate win
            blocked = False
            for m in moves:
                ns = sim.takeAction(m)
                if ns.isTerminal() and detect_winner(ns.board, ns.config) == opp:
                    sim = sim.takeAction(m)
                    blocked = True
                    break

            # 3. Otherwise random
            if not blocked:
                sim = sim.takeAction(random.choice(moves))

    return sim.getReward()


# -------- GRANDMASTER: OPTIMAL STRATEGY --------
def rollout_grandmaster(state):
    """
    Grandmaster strategy based on game theory research:

    Research sources confirm optimal tic-tac-toe strategy:
    - Wikipedia Tic-Tac-Toe optimal strategy
    - Newell & Simon 1972 algorithm
    - Multiple strategy guides

    Priority order:
    1. Win immediately
    2. Block opponent win
    3. Create fork (2+ winning threats)
    4. Play center (most valuable - 4 winning lines)
    5. Play corner (second-best - 3 winning lines each)
    6. Play edge (last resort - 2 winning lines)

    Key insight: Center should ALWAYS be preferred over corners
    when no tactical moves (win/block/fork) are available.
    """
    sim = state

    while not sim.isTerminal():
        moves = sim.getPossibleActions()
        if not moves:
            break

        curr = sim.current_player
        opp = "O" if curr == "X" else "X"

        # 1. WIN IMMEDIATELY
        win_move = can_win_next_move(sim.board, curr, sim.config)
        if win_move and win_move in moves:
            sim = sim.takeAction(win_move)
            continue

        # 2. BLOCK OPPONENT WIN
        block_move = can_win_next_move(sim.board, opp, sim.config)
        if block_move and block_move in moves:
            sim = sim.takeAction(block_move)
            continue

        # 3. CREATE FORK
        fork_move = find_fork_move(sim.board, curr, sim.config, moves)
        if fork_move:
            sim = sim.takeAction(fork_move)
            continue

        # 4. PLAY CENTER (highest priority positional move)
        # Research shows: center is ALWAYS better than corners when available
        if sim.config.size == 3:
            for m in moves:
                if m.row == 1 and m.col == 1:
                    sim = sim.takeAction(m)
                    break
            else:
                # Center not available, continue to next strategy
                pass

            # If we took center, continue to next iteration
            if sim.current_player != curr:
                continue

        # 5. PLAY CORNER (second-best positional move)
        if sim.config.size == 3:
            corners = [(0, 0), (0, 2), (2, 0), (2, 2)]
            corner_moves = [m for m in moves if (m.row, m.col) in corners]
            if corner_moves:
                sim = sim.takeAction(random.choice(corner_moves))
                continue

        # 6. PLAY EDGE (last resort)
        sim = sim.takeAction(random.choice(moves))

    return sim.getReward()


# ============================================================
# MAIN MCTS
# ============================================================
class MCTS:
    def __init__(self, iterationLimit, explorationConstant, rolloutPolicy):
        self.iterationLimit = iterationLimit
        self.c = explorationConstant
        self.rolloutPolicy = rolloutPolicy
        self.root = None

    def search(self, initialState):
        self.root = MCTSNode(initialState)

        for _ in range(self.iterationLimit):
            leaf = self._select(self.root)
            reward = self.rolloutPolicy(leaf.state)
            self._backpropagate(leaf, reward)

        # Pick move with most visits
        best_move = None
        max_visits = -1

        for move, child in self.root.children.items():
            if child.numVisits > max_visits:
                max_visits = child.numVisits
                best_move = move

        return best_move

    def _select(self, node):
        while not node.state.isTerminal():
            if not node.is_fully_expanded():
                return self._expand(node)
            node = node.best_child(self.c)
        return node

    def _expand(self, node):
        for move in node.state.getPossibleActions():
            if move not in node.children:
                nxt = node.state.takeAction(move)
                child = MCTSNode(nxt, parent=node)
                node.children[move] = child
                return child
        raise RuntimeError("No moves to expand")

    def _backpropagate(self, node, reward):
        while node is not None:
            node.numVisits += 1
            node.totalReward += reward
            node = node.parent


def mcts(iterationLimit, explorationConstant, difficulty):
    if difficulty == "easy":
        policy = rollout_easy
    elif difficulty == "medium":
        policy = rollout_medium
    elif difficulty == "hard":
        policy = rollout_hard
    elif difficulty == "grandmaster":
        policy = rollout_grandmaster
    else:
        policy = rollout_hard

    return MCTS(
        iterationLimit=iterationLimit,
        explorationConstant=explorationConstant,
        rolloutPolicy=policy,
    )