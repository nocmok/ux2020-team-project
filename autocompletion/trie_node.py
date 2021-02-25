class TrieNode:
    def __init__(self):
        self.index_from_letter = [-1 for i in range(0, 33)]
        self.dataset_max_frequency_line = -1
        self.dataset_max_frequency = -1
        self.parent_letter = '1' # temp

    def print_trie_node(self): # temp
        print(self.parent_letter, self.dataset_max_frequency, self.dataset_max_frequency_line)