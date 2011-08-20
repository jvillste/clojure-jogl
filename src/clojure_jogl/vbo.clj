(ns clojure-jogl.vbo
  (:import [java.nio IntBuffer FloatBuffer]
           [java.awt Frame BorderLayout Font]
           [java.awt.event WindowListener WindowAdapter KeyListener KeyEvent]
           [javax.media.opengl GLEventListener GL2 GLAutoDrawable GLCapabilities GLProfile]
           [javax.media.opengl.awt GLCanvas]
           [javax.media.opengl.glu GLU]
           [javax.swing JFrame]
           [java.nio FloatBuffer]))

(def *buffer-ids* (atom {}))

(defn generate-vbo [gl key]
  (let [id-array (int-array 1)]
    (.glGenBuffers gl 1 id-array 0)
    (reset! *buffer-ids* (assoc @*buffer-ids* key (first id-array)))
    (println (str "generated buffer " (first id-array)))
    (first id-array)))

(def *vertex-buffer* (atom nil))


(defn get-vbo [key] (key @*buffer-ids*))

(def canvas (doto (GLCanvas. (GLCapabilities. (GLProfile/get GLProfile/GL2)))
              (.setSize 400 400)
              (.addGLEventListener (proxy [GLEventListener] []

                                     (reshape  [#^GLAutoDrawable drawable x y w h]
                                       (println "reshape")
                                       (let [width (.getWidth drawable)
                                             height (.getHeight drawable)]
                                         (doto (.getGL2 (.getGL drawable))
                                           (.glViewport 0 0 width height )
                                           (.glMatrixMode GL2/GL_PROJECTION )
                                           (.glLoadIdentity)
                                           (.glOrtho 0 width height 0 0 1)
                                           (.glMatrixMode GL2/GL_MODELVIEW))))

                                     (init  [#^GLAutoDrawable drawable]
                                       (println "init")
                                       (let [gl (.getGL2 (.getGL drawable))]
                                         (.glClearColor gl 0.0 0.0 0.0 1.0)
                                         (.glColor3f gl 0.0 1.0 0.0 )
                                         (.glDisable gl GL2/GL_LIGHTING)
                                         (.glDisable gl GL2/GL_TEXTURE_2D)


                                         (.glEnableClientState gl GL2/GL_VERTEX_ARRAY)


                                         ;; Create the vertex bufer

                                         (generate-vbo gl :vertex-buffer)

                                         (let [width (.getWidth drawable)
                                               height (.getHeight drawable)]

                                           (reset! *vertex-buffer* (float-array [0 0 0
                                                                                width 0 0
                                                                                (/ width 2)  height 0]))
                                           (.glBindBuffer gl GL2/GL_ARRAY_BUFFER (get-vbo :vertex-buffer))
                                           (.glBufferData gl
                                                          GL2/GL_ARRAY_BUFFER
                                                          (alength @*vertex-buffer*)
                                                          (FloatBuffer/wrap @*vertex-buffer*)
                                                          GL2/GL_STATIC_DRAW))



                                         ;; Create index buffer

                                         (generate-vbo gl :index-buffer)

                                         (.glBindBuffer gl GL2/GL_ELEMENT_ARRAY_BUFFER (get-vbo :index-buffer))
                                         (let [index-buffer (int-array [0 1 2])]
                                           (.glBufferData gl
                                                          GL2/GL_ELEMENT_ARRAY_BUFFER
                                                          (alength index-buffer)
                                                          (IntBuffer/wrap index-buffer)
                                                          GL2/GL_STATIC_DRAW))))



                                     (dispose [#^GLAutoDrawable drawable]
                                       (println "Dispose")
                                       (.glDeleteBuffers (.getGL2 (.getGL drawable))
                                                         1
                                                         (int-array (get-vbo :vertex-buffer))
                                                         0))

                                     (display  [#^GLAutoDrawable drawable]

                                       (let [width (.getWidth drawable)
                                             height (.getHeight drawable)]
                                         (println (str "display " width " " height " buffer id: " (get-vbo :vertex-buffer)))
                                         (doto (.getGL2 (.getGL drawable))
                                           (.glClear GL2/GL_COLOR_BUFFER_BIT)
                                           (.glLoadIdentity)

                                           ;; (.glColor3f 0.0 1.0 0.0)
                                           ;; (.glBegin GL2/GL_TRIANGLES)
                                           ;; (.glVertex3f (aget *vertex-buffer* 0)
                                           ;;              (aget *vertex-buffer* 1)
                                           ;;              (aget *vertex-buffer* 2))
                                           ;; (.glVertex3f (aget *vertex-buffer* 3)
                                           ;;              (aget *vertex-buffer* 4)
                                           ;;              (aget *vertex-buffer* 5))
                                           ;; (.glVertex3f (aget *vertex-buffer* 6)
                                           ;;              (aget *vertex-buffer* 7)
                                           ;;              (aget *vertex-buffer* 8))

                                           ;; (.glEnd)

                                           (.glColor3f 1.0 0.0 0.0)
                                           ;;(.glEnableClientState GL2/GL_VERTEX_ARRAY)

                                           (.glBindBuffer GL2/GL_ARRAY_BUFFER (get-vbo :vertex-buffer))
                                           (.glVertexPointer 3 GL2/GL_FLOAT 0 (long 0))

                                           (.glBindBuffer GL2/GL_ARRAY_BUFFER (get-vbo :index-buffer))
                                           (.glDrawElements GL2/GL_TRIANGLES 3 GL2/GL_UNSIGNED_INT (long 0))
;;                                           (.glDrawArrays GL2/GL_TRIANGLES 0 9)

                                           (.glFlush)

                                           ;;(.glBindBuffer GL2/GL_ARRAY_BUFFER 0)
                                           ;;(.glDisableClientState GL2/GL_VERTEX_ARRAY)


                                           )))))))


(def frame (new JFrame "VBO test"))

(.add (.getContentPane frame)
      canvas BorderLayout/CENTER)

(doto frame
  (.setSize 400 400)
  (.addWindowListener (proxy [WindowAdapter] []
                        (windowClosing [e] (.dispose frame))))
  (.setVisible true))
