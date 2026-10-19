import json
import numpy as np

class PolicyPreprocessor:
    def __init__(self, use_gm_metadata=False):
        self.EMPTY = 0
        self.AI = 1
        self.OPP = 2
        self.use_gm_metadata = use_gm_metadata

    def parse_board(self, board_str):
        """Parse board from JSON string"""
        return json.loads(board_str)

    def encode_board(self, board_matrix, player_mark):
        """Encode board from AI perspective (AI=1, OPP=2, EMPTY=0)"""
        if player_mark == "X":
            ai, opp = "X", "O"
        else:
            ai, opp = "O", "X"

        encoded = []
        for r in range(3):
            for c in range(3):
                cell = board_matrix[r][c]
                if cell == ai:
                    encoded.append(self.AI)
                elif cell == opp:
                    encoded.append(self.OPP)
                else:
                    encoded.append(self.EMPTY)
        return np.array(encoded, dtype=np.float32)

    def encode_legal_mask(self, board_matrix):
        """Encode legal moves as binary mask"""
        mask = []
        for r in range(3):
            for c in range(3):
                mask.append(1.0 if board_matrix[r][c] == "" else 0.0)
        return np.array(mask, dtype=np.float32)

    def parse_policy_vector(self, policy_json):
        """Parse policy vector from various formats"""
        if isinstance(policy_json, dict):
            obj = policy_json
        elif isinstance(policy_json, str):
            cleaned = policy_json.replace("'", '"')
            try:
                obj = json.loads(cleaned)
            except:
                try:
                    obj = eval(policy_json)
                except:
                    print("ERROR parsing:", policy_json)
                    obj = {}
        else:
            print("UNKNOWN TYPE:", type(policy_json))
            obj = {}

        # Build 9-element vector
        vec = np.zeros(9, dtype=np.float32)
        for key, value in obj.items():
            r, c = map(int, key.split(","))
            vec[r * 3 + c] = float(value)

        return vec

    def transform_dataset(self, df):
        """Transform dataframe to training data"""
        X_list = []
        y_list = []

        for _, row in df.iterrows():
            # Parse board
            board = json.loads(row["board"])
            player = row["player_mark"]

            # Core features: board + legal moves
            board_vec = self.encode_board(board, player)
            legal_vec = self.encode_legal_mask(board)

            if self.use_gm_metadata:
                # Optional: add GM metadata as features
                meta = np.array([
                    float(row["gm_best_q_value"]),
                    float(row["gm_move_confidence"]),
                    float(row["gm_explored_actions"]) / 9.0,  # Normalize
                    float(row["gm_mcts_tree_size"]) / 1000.0,  # Normalize
                    float(row["gm_mcts_iterations"]) / 1000.0,  # Normalize
                ], dtype=np.float32)
                x = np.concatenate([board_vec, legal_vec, meta])
            else:
                # Simple: just board + legal moves
                x = np.concatenate([board_vec, legal_vec])

            # Target: GM policy vector
            policy = self.parse_policy_vector(row["gm_policy_vector_json"])

            X_list.append(x)
            y_list.append(policy)

        return np.vstack(X_list), np.vstack(y_list)

    def encode_inference_input(self, board_matrix, player_mark):
        """Encode input for inference (must match training features!)"""
        board_vec = self.encode_board(board_matrix, player_mark)
        legal_vec = self.encode_legal_mask(board_matrix)

        if self.use_gm_metadata:
            # Use neutral/average values during inference
            meta = np.array([0.0, 0.5, 0.5, 0.5, 0.5], dtype=np.float32)
            return np.concatenate([board_vec, legal_vec, meta])
        else:
            return np.concatenate([board_vec, legal_vec])

    def decode_move(self, class_id):
        """Convert class ID to row, col"""
        return class_id // 3, class_id % 3