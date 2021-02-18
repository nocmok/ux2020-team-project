import sys
import json
import pandas as pd
import numpy as np
from trie import Trie

import time

if __name__ != '__main__':
    sys.exit()

layout_pathes = []
baseline_layout_path = ""
dict_path = ""
baseline_layout = None

keys = ['s', 'd', 'f', 'g', 'h', 'j', 'k', 'l']
keys_to_index = {key: keys.index(key) for key in keys}

chars = ['а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о',
         'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', "ы", 'ь', 'э', 'ю', 'я']


def parse_argv():
    global layout_pathes
    global dict_path
    global baseline_layout_path

    if len(sys.argv) < 4:
        print(
            'usage: python metrics.py [path/to/layout#1, ...] [path/to/baseline/layout] [path/to/dictionary]')

    layout_pathes = [path for path in sys.argv[1:-2]]
    # for i in range(1, 29):
        # layout_pathes.append(f'keyboard_prefix_{i}.json')

    baseline_layout_path = sys.argv[-2]
    dict_path = sys.argv[-1]


def parse_layout(path):
    layout_str = ""
    with open(path) as f:
        layout_str = f.read()
    layout = json.loads(layout_str)
    return layout


def get_file_name(path):
    return path.split('/')[-1]


class KeyboardLayout:

    def __init__(self, dict_):
        self._dict = dict_
        self._depths = dict()
        self._to_key = dict()
        for key, chars in self._dict.items():
            for index, c in enumerate(chars):
                self._depths[c] = index + 1
                self._to_key[c] = key

    def char_key(self, char):
        return self._to_key[char]

    def char_depth(self, char):
        """How much taps to perform in order to select specified character"""
        return self._depths[char]


def kspw_exclude_space(trie, layout, word, line):
    ks = 0
    predict_threshold = trie.keystrokes_per_word(word, line)
    for i, c in enumerate(word):
        if i + 1 > predict_threshold:
            break
        ks += layout.char_depth(c)
    return ks


def kspw(trie, layout, word, line):
    ks = 0
    predict_threshold = trie.keystrokes_per_word(word, line)
    for i, c in enumerate(word):
        if i + 1 > predict_threshold:
            break
        ks += layout.char_depth(c)
    return ks + 1 if predict_threshold < len(word) else ks


def kspc_exclude_space(trie, layout, dict_):
    kw = np.array(list(map(lambda i: kspw_exclude_space(
        trie, layout, dict_['Word'].values[i], i), dict_['#'].values)))
    cw = np.array(list(map(lambda w: len(w), dict_['Word'].values)))
    kspc = (kw * dict_['Freq'].values).sum() / \
        (cw * dict_['Freq'].values).sum()
    return kspc


def kspc(trie, layout, dict_):
    kw = np.array(list(map(lambda i: kspw(trie, layout, dict_[
                  'Word'].values[i], i), dict_['#'].values)))
    cw = np.array(list(map(lambda w: len(w), dict_['Word'].values)))
    kspc = (kw * dict_['Freq'].values).sum() / \
        (cw * dict_['Freq'].values).sum()
    return kspc


def lp(trie, layout, dict_):
    lp = 0
    for char in chars:
        lp += abs(keys_to_index[layout.char_key(char)] -
                  keys_to_index[baseline_layout.char_key(char)])
    return lp


kspc_exclude_space.metric_name = 'kspc(exclude space)'
kspc.metric_name = 'kspc'
lp.metric_name = 'lp'


def compute_metrics(trie, layouts, dict_):
    metrics = [kspc_exclude_space, kspc, lp]
    df = pd.DataFrame(columns=[metric.metric_name for metric in metrics])

    for i in range(0, len(layouts)):
        df.loc[get_file_name(layout_pathes[i])] = [metric(
            trie, layouts[i], dict_) for metric in metrics]
    return df


parse_argv()
layouts = [KeyboardLayout(parse_layout(path)) for path in layout_pathes]
baseline_layout = KeyboardLayout(parse_layout(baseline_layout_path))

dict_ = pd.read_csv(dict_path)
dict_.insert(0, column='#', value=range(0, dict_.shape[0]))

trie = Trie()
trie.add_dataset(dict_)
metrics = compute_metrics(trie, layouts, dict_)

print(metrics.to_string())
