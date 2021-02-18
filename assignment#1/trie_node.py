class TrieNode:
    index_from_letter = [-1 for i in range(0, 33)]
    dataset_max_frequency_line = -1
    dataset_max_frequency = -1
    parent_letter = '1' # temp

    def print_trie_node(self):
        print(self.parent_letter, self.dataset_max_frequency, self.dataset_max_frequency_line)

# trie_node = TrieNode()
# print(len(trie_node.index_from_letter))
# print(trie_node.index_from_letter)