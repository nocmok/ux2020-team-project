import numpy as np
import pandas as pd
import json

class KeyboardFamily:
    def __init__(self):
        self.words = pd.read_csv('../dictionary.csv').Word.tolist()
        self.max_word_len = 0
        for i in self.words:
            self.max_word_len = max(self.max_word_len, len(i))

    def get_letter_index(self, letter):
        return ord(letter.lower()) - ord('а')

    def create_keyboard(self, prefix_len):
        current_layout = {'s' : [], 'd' : [], 'f' : [], 'g' : [], 'h' : [], 'j' : [], 'k' : [], 'l' : []}
        buttons = ['s', 'd', 'f', 'g', 'h', 'j', 'k', 'l']
        current_button_number = 0
        letter_used = [False for i in range(0, 33)]

        for i in range(0, self.max_word_len, prefix_len):
            letter_frequency = [[i, 0] for i in range(0, 33)] 
            for current_word in self.words:
                for index in range(i, min(len(current_word), i + prefix_len)):
                    letter_frequency[self.get_letter_index(current_word[index])][1] += 1

            letter_frequency.sort(key = lambda x: x[1])
            letter_frequency.reverse()

            for frequency in letter_frequency:
                if frequency[1] == 0:
                    break
                if letter_used[frequency[0]] == False:
                    letter_used[frequency[0]] = True
                    current_layout[buttons[current_button_number]].append(chr(frequency[0] + ord('а')))
                    current_button_number = (current_button_number + 1) % 8

        return current_layout

    def generate_keyboard_family(self):
        for i in range(1, self.max_word_len):
            keyboard_dict = self.create_keyboard(i)
            with open("keyboard_prefix_" + str(i) + ".json", "w") as write_file:
                json.dump(keyboard_dict, write_file, ensure_ascii=False)