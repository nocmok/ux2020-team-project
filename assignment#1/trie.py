import trie_node
import numpy as np
import pandas as pd

class Trie:
    trie_nodes = [trie_node.TrieNode()]

    def update_node_frequency(self, index, frequency, word_line):
        if self.trie_nodes[index].dataset_max_frequency < frequency:
            self.trie_nodes[index].dataset_max_frequency = frequency
            self.trie_nodes[index].dataset_max_frequency_line = word_line

    def get_next_index(self, current_index, letter, frequency, word_line):
        letter_index = ord(letter.lower()) - ord('a')
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

    # def add_dataset(self, dataset):

    # def keystrokes_per_word(self, word):

    def print_trie(self): # temp
        for i in self.trie_nodes:
            i.print_trie_node()


# trie1 = Trie()
# trie1.add_word('qwer', 1, 1)
# trie1.add_word('qxcv', 2, 2)
# trie1.add_word('qxpo', 1, 3)
# trie1.print_trie()
# trie1 = Trie()
# trie_node = trie_node.TrieNode()
# print(len(trie_node.index_from_letter))
# print(trie_node.index_from_letter)
# print(len(trie1.trie_nodes))