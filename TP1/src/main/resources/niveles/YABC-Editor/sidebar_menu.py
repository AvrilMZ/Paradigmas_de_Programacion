import tkinter as tk

ELEMENTS = {
    "player1": "Player 1",
    "player2": "Player 2",
    "enemy_regular": "Enemy Tank (Regular)",
    "enemy_heavy": "Enemy Tank (Heavy)",
    "enemy_fast": "Enemy Tank (Fast)",
    "enemy_powerful": "Enemy Tank (Powerful)",
    "base": "Base",
    "brick": "Brick",
    "steel": "Steel",
    "water": "Water",
    "forest": "Forest",
    "empty": "Empty"
}

COLORS = {
    "player1": "#90ee90",       # Light Green
    "player2": "#006400",       # Dark Green
    "enemy_regular": "#8b4513", # Brown
    "enemy_heavy": "#ffd700",   # Yellow
    "enemy_fast": "#104e8b",    # Dark Blue
    "enemy_powerful": "#ff0000",# Red
    "base": "#ffffff",          # White
    "brick": "#a9a9a9",         # Gray
    "steel": "#555555",         # Dark Gray
    "water": "#87ceeb",         # Light Blue
    "forest": "#228b22",        # Forest Green
    "empty": "#000000"          # Black
}

class SidebarMenu:
    def __init__(self, parent, on_select_element, on_clear, on_export, on_open_xml):
        self.frame = tk.Frame(parent)
        self.frame.pack(side=tk.LEFT, fill=tk.Y, padx=5, pady=5)

        self.on_select_element = on_select_element
        self.on_clear = on_clear
        self.on_export = on_export
        self.on_open_xml = on_open_xml

        tk.Label(self.frame, text="Select Element:", font=("Arial", 12, "bold")).pack(pady=5)

        for key, label in ELEMENTS.items():
            row = tk.Frame(self.frame)
            row.pack(pady=2, anchor="w")

            color_box = tk.Canvas(row, width=20, height=20, highlightthickness=1)
            color_box.create_rectangle(0, 0, 20, 20, fill=COLORS.get(key, "#000000"), outline="#333")
            color_box.pack(side=tk.LEFT)

            btn = tk.Button(row, text=label, width=20,
                            command=lambda k=key: self.on_select_element(k))
            btn.pack(side=tk.LEFT)

        tk.Button(self.frame, text="Clear Grid", command=self.on_clear).pack(pady=10)

        self.export_btn = tk.Button(self.frame, text="Export XML", command=self.on_export, state=tk.DISABLED)
        self.export_btn.pack(pady=5)

        tk.Button(self.frame, text="Open XML", command=self.on_open_xml).pack(pady=5)

        self.status_label = tk.Label(self.frame, text="", fg="red", font=("Arial", 10))
        self.status_label.pack(pady=5)

    def update_status(self, player1_ok, player2_ok, base_ok):
        missing = []
        if not player1_ok:
            missing.append("Player 1")
        if not player2_ok:
            missing.append("Player 2")
        if not base_ok:
            missing.append("Base")

        if missing:
            self.status_label.config(text="Missing: " + ", ".join(missing))
            self.export_btn.config(state=tk.DISABLED)
        else:
            self.status_label.config(text="Ready to export")
            self.export_btn.config(state=tk.NORMAL)