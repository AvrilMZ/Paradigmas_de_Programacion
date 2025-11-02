import tkinter as tk
from tkinter import filedialog, messagebox
import xml.etree.ElementTree as ET
import xml.dom.minidom
from lxml import etree

from level_canvas import LevelCanvas, CELL_SIZE, CANVAS_WIDTH, CANVAS_HEIGHT
from sidebar_menu import SidebarMenu

class LevelEditorApp:
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("Battle City Level Editor")

        self.canvas = LevelCanvas(self.root, self.on_canvas_change)
        self.menu = SidebarMenu(
            self.root,
            on_select_element=self.canvas.set_selected_element,
            on_clear=self.canvas.clear_grid,
            on_export=self.export_xml,
            on_open_xml=self.open_and_validate_xml
        )

        self.on_canvas_change()
        self.root.mainloop()

    def on_canvas_change(self):
        state = self.canvas.get_state()
        player1_ok = state["player_positions"]["player1"] is not None
        player2_ok = state["player_positions"]["player2"] is not None
        base_ok = state["base_position"] is not None
        self.menu.update_status(player1_ok, player2_ok, base_ok)

    def export_xml(self):
        state = self.canvas.get_state()
        grid = state["grid"]
        player_positions = state["player_positions"]
        base_position = state["base_position"]

        root = ET.Element("levelConfig", {
            "gameName": "BattleCity",
            "version": "1.0",
            "levelIndex": "1",
            "name": "Level 1"
        })

        level = ET.SubElement(root, "level", {
            "width": str(CANVAS_WIDTH),
            "height": str(CANVAS_HEIGHT),
            "cols": str(len(grid[0])),
            "rows": str(len(grid))
        })

        players = ET.SubElement(level, "players")
        enemies = ET.SubElement(level, "enemies")
        static_objects = ET.SubElement(level, "staticObjects")

        for pid, (py, px) in player_positions.items():
            ET.SubElement(players, "player", {
                "id": pid,
                "x": str(px),
                "y": str(py)
            })

        for y in range(len(grid)):
            for x in range(len(grid[0])):
                tile = grid[y][x]
                px = x * CELL_SIZE + CELL_SIZE // 2
                py = y * CELL_SIZE + CELL_SIZE // 2

                if tile.startswith("enemy_"):
                    type_map = {
                        "enemy_regular": "regularEnemy",
                        "enemy_heavy": "heavyEnemy",
                        "enemy_fast": "fastEnemy",
                        "enemy_powerful": "powerfulEnemy"
                    }
                    ET.SubElement(enemies, "enemy", {
                        "id": f"enemy{y}_{x}",
                        "type": type_map[tile],
                        "x": str(px),
                        "y": str(py)
                    })

                elif tile == "brick":
                    ET.SubElement(static_objects, "staticObject", {
                        "type": "brickBlock",
                        "x": str(px),
                        "y": str(py)
                    })

                elif tile == "steel":
                    ET.SubElement(static_objects, "staticObject", {
                        "type": "steelBlock",
                        "x": str(px),
                        "y": str(py)
                    })

                elif tile == "water":
                    ET.SubElement(static_objects, "staticObject", {
                        "type": "waterBlock",
                        "x": str(px),
                        "y": str(py)
                    })

                elif tile == "forest":
                    ET.SubElement(static_objects, "staticObject", {
                        "type": "forestBlock",
                        "x": str(px),
                        "y": str(py)
                    })

                elif tile == "base":
                    ET.SubElement(static_objects, "staticObject", {
                        "type": "baseBlock",
                        "x": str(px),
                        "y": str(py)
                    })

        file_path = filedialog.asksaveasfilename(defaultextension=".xml",
                                                 filetypes=[("XML files", "*.xml")])
        if file_path:
            rough_string = ET.tostring(root, 'utf-8')
            reparsed = xml.dom.minidom.parseString(rough_string)
            pretty_xml = reparsed.toprettyxml(indent="    ")

            with open(file_path, "w", encoding="utf-8") as f:
                f.write(pretty_xml)

            messagebox.showinfo("Export Successful", f"Level saved to:\n{file_path}")

    def open_and_validate_xml(self):
        file_path = filedialog.askopenfilename(filetypes=[("XML files", "*.xml")])
        if not file_path:
            return

        try:
            with open("levelConfig.xsd", "rb") as schema_file:
                schema_doc = etree.parse(schema_file)
                schema = etree.XMLSchema(schema_doc)

            xml_doc = etree.parse(file_path)
            schema.assertValid(xml_doc)

            root = xml_doc.getroot()
            level = root.find("level")

            self.canvas.clear_grid()

            # Load players
            for player in level.find("players").findall("player"):
                pid = player.get("id")
                px = int(player.get("x"))
                py = int(player.get("y"))
                x = (px - CELL_SIZE // 2) // CELL_SIZE
                y = (py - CELL_SIZE // 2) // CELL_SIZE
                self.canvas.grid[y][x] = pid
                self.canvas.player_positions[pid] = (py, px)
                self.canvas.canvas.itemconfig(self.canvas.cell_ids[y][x], fill=self.canvas.COLORS[pid])

            # Load base
            for obj in level.find("staticObjects").findall("staticObject"):
                if obj.get("type") == "baseBlock":
                    px = int(obj.get("x"))
                    py = int(obj.get("y"))
                    x = (px - CELL_SIZE // 2) // CELL_SIZE
                    y = (py - CELL_SIZE // 2) // CELL_SIZE
                    self.canvas.grid[y][x] = "base"
                    self.canvas.base_position = (py, px)
                    self.canvas.canvas.itemconfig(self.canvas.cell_ids[y][x], fill=self.canvas.COLORS["base"])

            # Load enemies
            for enemy in level.find("enemies").findall("enemy"):
                px = int(enemy.get("x"))
                py = int(enemy.get("y"))
                x = (px - CELL_SIZE // 2) // CELL_SIZE
                y = (py - CELL_SIZE // 2) // CELL_SIZE
                type_map = {
                    "regularEnemy": "enemy_regular",
                    "heavyEnemy": "enemy_heavy",
                    "fastEnemy": "enemy_fast",
                    "powerfulEnemy": "enemy_powerful"
                }
                tile = type_map.get(enemy.get("type"))
                if tile:
                    self.canvas.grid[y][x] = tile
                    self.canvas.canvas.itemconfig(self.canvas.cell_ids[y][x], fill=self.canvas.COLORS[tile])

            # Load other static objects
            for obj in level.find("staticObjects").findall("staticObject"):
                if obj.get("type") != "baseBlock":
                    px = int(obj.get("x"))
                    py = int(obj.get("y"))
                    x = (px - CELL_SIZE // 2) // CELL_SIZE
                    y = (py - CELL_SIZE // 2) // CELL_SIZE
                    type_map = {
                        "brickBlock": "brick",
                        "steelBlock": "steel",
                        "waterBlock": "water",
                        "forestBlock": "forest"
                    }
                    tile = type_map.get(obj.get("type"))
                    if tile:
                        self.canvas.grid[y][x] = tile
                        self.canvas.canvas.itemconfig(self.canvas.cell_ids[y][x], fill=self.canvas.COLORS[tile])

            self.on_canvas_change()
            messagebox.showinfo("Success", "Level loaded and validated successfully.")

        except Exception as e:
            messagebox.showerror("Validation Error", f"Invalid XML:\n{str(e)}")

if __name__ == "__main__":
    LevelEditorApp()