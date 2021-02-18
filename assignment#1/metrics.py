import sys
import json
import pandas as pd
import numpy as np
from trie import Trie

import time

if __name__ != '__main__':
    sys.exit()

layout_pathes = []
dict_path = ""


def parse_argv():
    global layout_pathes
    global dict_path
    if len(sys.argv) < 2:
        print(
            'usage: python metrics.py [path/to/layout#1, ...] [path/to/dictionary]')

    layout_pathes = [path for path in sys.argv[1:-1]]

    # for i in range(1, 29):
        # layout_pathes.append(f'keyboard_prefix_{i}.json')

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
        for key, chars in self._dict.items():
            for index, c in enumerate(chars):
                self._depths[c] = index + 1

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
    kw = np.array(list(map(lambda i : kspw_exclude_space(trie, layout, dict_['Word'].values[i], i), dict_['#'].values)))
    cw = np.array(list(map(lambda w : len(w), dict_['Word'].values)))
    kspc = (kw * dict_['Freq'].values).sum() / (cw * dict_['Freq'].values).sum()
    return kspc


def kspc(trie, layout, dict_):
    kw = np.array(list(map(lambda i : kspw(trie, layout, dict_['Word'].values[i], i), dict_['#'].values)))
    cw = np.array(list(map(lambda w : len(w), dict_['Word'].values)))
    kspc = (kw * dict_['Freq'].values).sum() / (cw * dict_['Freq'].values).sum()
    return kspc

def lp(trie, layout, dict_):
    return None


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

dict_ = pd.read_csv(dict_path)
dict_.insert(0, column='#', value=range(0, dict_.shape[0]))

trie = Trie()
trie.add_dataset(dict_)

before = time.time()
metrics = compute_metrics(trie, layouts, dict_)
after =  time.time()
print(round((after - before) * 1000))

print(metrics.to_string())
