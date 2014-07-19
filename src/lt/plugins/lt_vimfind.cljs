;; TBD:
;; [*] scrolling
;; [*] css
;; [*] keymap
;; [ ] WHY RELOADING BEHAVIORS???

(ns lt.plugins.lt-vimfind
  (:require [lt.object :as object]
            [lt.util.dom :as dom]
            [lt.objs.editor :as editor]
            [lt.objs.editor.pool :as pool]
            [lt.objs.context :as context]
            [lt.objs.statusbar :as statusbar]
            [lt.objs.command :as cmd])
  (:require-macros [lt.macros :refer [defui behavior]]))


(defn scroll [ed old-pos new-pos]
  (let [height (-> ed editor/->cm-ed .getScrollInfo .-clientHeight)
        old-y (.-top (.charCoords (editor/->cm-ed ed) (clj->js old-pos) "local"))
        new-y (.-top (.charCoords (editor/->cm-ed ed) (clj->js new-pos) "local"))]
    (when (> (Math/abs (- old-y new-y)) height)
          (editor/center-cursor ed))))

(defn find-next [text backward?]
  (let [ed (pool/last-active)
        offset (if backward? (dec (count text)) 1)
        pos (editor/->cursor ed)]
    (editor/move-cursor ed (update-in pos [:ch] (partial + offset)))
    (js/CodeMirror.commands.find (editor/->cm-ed ed) text backward?)
    (js/CodeMirror.commands.clearSearch (editor/->cm-ed ed))
    (scroll ed pos (editor/->cursor ed))))

(object/object* ::vimfind-obj
                :behaviors #{::fix-search ::cancel-search ::search}
                :start-pos nil
                :backward? false
                :previous-search {:text nil :backward? false}
                :text nil
                :bar nil)

(def thing (object/create ::vimfind-obj))

(behavior ::fix-search
          :triggers #{:fix-search}
          :reaction (fn [this]
                      (when (:text @this)
                        (object/merge! this {:start-pos nil
                                             :previous-search (select-keys @this [:text :backward?])})
                        (object/destroy! (:bar @this))
                        (object/merge! this {:bar nil})
                        (js/CodeMirror.commands.clearSearch (editor/->cm-ed (pool/last-active)))
                        (editor/focus (pool/last-active)))))

(cmd/command {:command ::fix-search
              :desc "..."
              :hidden true
              :exec #(object/raise thing :fix-search)})

(behavior ::cancel-search
          :triggers #{:cancel-search}
          :reaction (fn [this]
                      (let [ed (pool/last-active)]
                        (editor/move-cursor ed (:start-pos @this))
                        (object/merge! this {:start-pos nil
                                             :text (-> @this :previous-search :text)
                                             :backward? (-> @this :previous-search :backward?)})
                        (object/destroy! (:bar @this))
                        (object/merge! this {:bar nil})
                        (js/CodeMirror.commands.clearSearch (editor/->cm-ed ed))
                        (editor/focus ed))))

(cmd/command {:command ::cancel-search
              :desc "..."
              :hidden true
              :exec #(object/raise thing :cancel-search)})

(behavior ::search
          :triggers #{:search}
          :reaction (fn [this direction]
                      (let [ed (pool/last-active)
                            {:keys [text start-pos backward?]} @this
                            backward? (if (= direction :backward) (not backward?) backward?)]
                        (when start-pos
                          (editor/move-cursor ed start-pos))
                        (find-next text backward?))))

(defui input [this]
  [:input.vimfind-input {:type "text"}]
  :keyup (fn [k]
           (this-as me
                    (object/merge! thing {:text (dom/val me)})
                    (object/raise thing :search)))
  :focus #(context/in! ::ctx this)
  :blur #(context/out! ::ctx))

(object/object* ::vimfind-bar
                :name "Ъ-search-bar"
                :order -1
                :height 30
                :shown false
                :init (fn [this]
                        [:div.vimfind-bar (input this)]))

(cmd/command {:command ::search
              :desc "Vimfind: search"
              :exec (fn [direction]
                      (let [backward? (= direction :backward)
                            bar (or (:bar @thing) (object/create ::vimfind-bar))]
                        (object/merge! thing {:start-pos (editor/->cursor (pool/last-active))
                                              :backward? backward?
                                              :text nil
                                              :bar bar})
                        (statusbar/add-container bar)
                        (dom/focus (dom/$ :input (object/->content bar)))))})

(cmd/command {:command ::find-next
              :desc "Vimfind: find next"
              :exec (fn [direction]
                      (object/raise thing :search direction))})

(behavior ::focus!
          :triggers #{:focus!}
          :reaction (fn [this]
                      (let [input (dom/$ :input (object/->content this))]
                        (dom/focus input))))
