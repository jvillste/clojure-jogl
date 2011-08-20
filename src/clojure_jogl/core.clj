(ns clojure-jogl.core)

(import '(java.awt Frame BorderLayout Font)
        '(java.awt.event WindowListener WindowAdapter KeyListener KeyEvent)
        '(javax.media.opengl GLEventListener GL GLAutoDrawable GLCapabilities GLDrawableFactory GLCanvas)
        '(javax.media.opengl.glu GLU)
        '(javax.swing JFrame)
        '(com.sun.opengl.util.j2d TextRenderer))

(def glu (new GLU))

(def textRenderer)


(def capabilities (GLCapabilities.))
(doto capabilities
  (.setDoubleBuffered true)
  (.setHardwareAccelerated true)
  (.setSampleBuffers true)
  (.setNumSamples 4))

(def canvas (GLCanvas. capabilities))

(def frame (new JFrame "Triangle"))

(.addGLEventListener canvas
                     (proxy [GLEventListener] []

                       (reshape  [#^GLAutoDrawable drawable x y w h]
                         (println "reshape")
                         (let [width (.getWidth drawable)
                               height (.getHeight drawable)]
                           (doto (.getGL drawable)
                             (.glViewport 0 0 width height )
                             (.glMatrixMode GL/GL_PROJECTION )
                             (.glLoadIdentity)
                             (.glOrtho 0 width height 0 0 1)
                             (.glMatrixMode GL/GL_MODELVIEW))))
                           
                           

                       (init  [#^GLAutoDrawable drawable]
                         (println "init")
                         (doto (.getGL drawable)
                           (.glClearColor 1.0 1.0 1.0 1.0)
                           (.glColor3f 1.0 0.0 0.0 )
                           (.glPointSize 4.0)

                           (.glDisable GL/GL_DEPTH_TEST)
                           
                           (.glEnable GL/GL_LINE_SMOOTH)
                           (.glEnable GL/GL_BLEND)
                           (.glBlendFunc GL/GL_SRC_ALPHA GL/GL_ONE_MINUS_SRC_ALPHA)
                           (.glHint GL/GL_LINE_SMOOTH_HINT GL/GL_DONT_CARE))
                         (def textRenderer (TextRenderer. (Font. "Arial" Font/BOLD 12))))


                       (dispose [#^GLAutoDrawable drawable])

                       (display  [#^GLAutoDrawable drawable]

                         (let [width (.getWidth drawable)
                               height (.getHeight drawable)]
                           (println (str "display " width " " height ))
                           (doto (.getGL drawable)
                             (.glClear GL/GL_COLOR_BUFFER_BIT)
                             (.glLoadIdentity)
                             (.glColor3f 1.0 0.0 0.0)

                             (.glBegin GL/GL_TRIANGLES)
                             (.glVertex2f 0 0)
                             (.glVertex2f width 0)
                             (.glVertex2f (/ width 2)  height)
                             (.glEnd)

                             (.glColor3f 0.0 1.0 0.0)
                             (.glBegin GL/GL_LINES)
                             (.glVertex2f 0 0)
                             (.glVertex2f width height)
                             (.glEnd))


                           (doto textRenderer
                             (.begin3DRendering)
                             (.setColor 0 0 1 1)
                             (.draw3D "fooa sfdpjas faslkfj asöf" (float 0) (float 0) (float 0) (float 1) )
                             (.end3DRendering))))))


(.add (.getContentPane frame)
      canvas BorderLayout/CENTER)

(doto frame
  (.setSize 400 400)
  (.addWindowListener (proxy [WindowAdapter] []
                        (windowClosing [e] (.dispose frame))))
  (.setVisible true))
