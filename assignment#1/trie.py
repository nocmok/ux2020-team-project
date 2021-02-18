import trie_node
import numpy as np
import pandas as pd

class Trie:
    def __init__(self):
        self.trie_nodes = [trie_node.TrieNode()]

    def get_letter_index(self, letter):
        return ord(letter.lower()) - ord('а')

    def update_node_frequency(self, index, frequency, word_line):
        if self.trie_nodes[index].dataset_max_frequency < frequency:
            self.trie_nodes[index].dataset_max_frequency = frequency
            self.trie_nodes[index].dataset_max_frequency_line = word_line

    def get_next_index(self, current_index, letter, frequency, word_line):
        letter_index = self.get_letter_index(letter)

        if self.trie_nodes[current_index].index_from_letter[letter_index] == -1:
            self.trie_nodes.append(trie_node.TrieNode())
            self.trie_nodes[current_index].index_from_letter[letter_index] = len(self.trie_nodes) - 1
            self.trie_nodes[len(self.trie_nodes) - 1].parent_letter = letter # temp

        self.update_node_frequency(self.trie_nodes[current_index].index_from_letter[letter_index], frequency, word_line)
        return self.trie_nodes[current_index].index_from_letter[letter_index]

    def add_word(self, word, frequency, word_line):
        current_index = 0
        for i in word:
            current_index = self.get_next_index(current_index, i, frequency, word_line)

    def add_dataset(self, dataset):
        dataset_len = dataset.shape[0]
        for i in range(0, dataset_len):
            self.add_word(dataset.Word[i], dataset.FreqPM[i], i)

    def keystrokes_per_word(self, word, line):
        current_index = 0
        keystrokes = 0

        for i in word:
            if self.trie_nodes[current_index].dataset_max_frequency_line == line:
                break
            keystrokes += 1
            current_index = self.trie_nodes[current_index].index_from_letter[self.get_letter_index(i)]

        return keystrokes


    def print_trie(self): # temp
        for i in self.trie_nodes:
            i.print_trie_node()