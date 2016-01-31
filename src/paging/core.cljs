(ns paging.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(defonce app-state (atom {:blocks-data [{:height "300px"  :id "A"}
                                        {:height "50px"   :id "B"}
                                        {:height "25px"   :id "C"}
                                        {:height "100px"  :id "D"}
                                        {:height "20px"   :id "E"}
                                        {:height "250px"  :id "F"}
                                        {:height "150px"  :id "G"}
                                        {:height "350px"  :id "H"}
                                        {:height "300px"  :id "I"}
                                        {:height "50px"   :id "J"}
                                        {:height "25px"   :id "K"}
                                        {:height "100px"  :id "L"}
                                        {:height "20px"   :id "M"}
                                        {:height "250px"  :id "N"}
                                        {:height "150px"  :id "O"}
                                        {:height "350px"  :id "P"}
                                        {:height "300px"  :id "Q"}
                                        {:height "50px"   :id "R"}
                                        {:height "25px"   :id "S"}
                                        {:height "100px"  :id "T"}
                                        {:height "20px"   :id "U"}
                                        {:height "250px"  :id "V"}
                                        {:height "150px"  :id "W"}
                                        {:height "350px"  :id "X"}
                                        {:height "300px"  :id "Y"}
                                        {:height "50px"   :id "Z"}
                                        {:height "25px"   :id "AA"}
                                        {:height "100px"  :id "AB"}
                                        {:height "20px"   :id "AC"}
                                        {:height "250px"  :id "AD"}
                                        {:height "150px"  :id "AE"}
                                        {:height "350px"  :id "AF"}]
                          :blocks-ui []
                          :pager 0}))

(defn bottom [element]
  (+ (- (.-offsetTop element) 
        (.-scrollTop element)) 
     (.-clientTop element) 
     (.-scrollHeight element)))

(defn container-height []
  (- (* (.-clientHeight js/document.documentElement) 
        (:pager @app-state))
     45))

(defn element-out-of-container? [element]
  (> (bottom element) 
     (container-height)))

(defn ^:export scroll-to-bottom []
  (->>
    (.-scrollHeight js/document.body)
    (js/window.scrollTo 0)))

(defn remove-last-block-ui! []
  (swap! app-state update :blocks-ui #(vec (drop-last %)))) 

(defn add-block-ui! [block]
  (swap! app-state update :blocks-ui conj block))

(defn pull-block-data! []
  (let [{:keys [blocks-data blocks-ui]} @app-state]
    (->>
      (drop (count blocks-ui) blocks-data)
      first 
      add-block-ui!)))

(defn block-view [{:keys [height id]} owner]
  (reify
    
    om/IDidMount
    (did-mount [_]
      (print "Om mounted " id)
      (if (element-out-of-container? (om/get-node owner))
        (do
          (remove-last-block-ui!)
          (scroll-to-bottom))
        (pull-block-data!)))
    
    om/IRender
    (render [_]
      (print "Om render " id)
      (dom/div (clj->js {:style
                          {:backgroundColor "#619ADE"
                           :height height
                           :margin "25px auto"}})
               id))))

(defn blocks-view [data owner]
  (reify
    
    om/IRender
    (render [this]
      (dom/div nil
        (apply dom/div nil
            (om/build-all block-view data))
        (dom/button #js {:onClick #(do 
                                     (swap! app-state update :pager inc)
                                     (pull-block-data!))}
                    "+")))))

(om/root
  blocks-view
  app-state
  {:target (. js/document (getElementById "app"))
   :path [:blocks-ui]})


(defn on-js-reload [])