lt-vimfind
----------

lt-vimfind is a Light Table plugin that make 'find' command
behave like in vim.

## Usage

lt-vimfind provides two commands, `search` and `find-next`. By
default, they are bound to the keys you would expect (see
[lt_vimfind.keymap](https://github.com/igorhub/lt-vimfind/blob/master/lt_vimfind.keymap)):

```clojure
{:editor.keys.vim.normal {"/" [(:lt.plugins.lt-vimfind/search :forward)]
                          "?" [(:lt.plugins.lt-vimfind/search :backward)]
                          "n" [(:lt.plugins.lt-vimfind/find-next :forward)]
                          "N" [(:lt.plugins.lt-vimfind/find-next :backward)]}}
```

## Contributing

In its current state, lt-vimfind suits all may searching needs
(I have very few of them). However, if you find the plugin
useful but lacking something important (or acting annoying in
some way), you should definitely post an issue in the github's
tracking system.
