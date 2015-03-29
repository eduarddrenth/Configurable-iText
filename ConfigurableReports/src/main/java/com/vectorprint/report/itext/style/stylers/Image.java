/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vectorprint.report.itext.style.stylers;

/*
 * #%L
 * VectorPrintReport4.0
 * %%
 * Copyright (C) 2012 - 2013 VectorPrint
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.vectorprint.VectorPrintException;
import com.vectorprint.VectorPrintRuntimeException;
import com.vectorprint.configuration.EnhancedMap;
import com.vectorprint.configuration.parameters.BooleanParameter;
import com.vectorprint.configuration.parameters.PasswordParameter;
import com.vectorprint.configuration.parameters.URLParameter;
import com.vectorprint.report.ReportConstants;
import com.vectorprint.report.itext.BaseReportGenerator;
import com.vectorprint.report.itext.ImageLoader;
import com.vectorprint.report.itext.LayerManager;
import com.vectorprint.report.itext.debug.DebugHelper;
import com.vectorprint.report.itext.ImageLoaderAware;
import com.vectorprint.report.itext.VectorPrintDocument;
import com.vectorprint.report.itext.style.StylerFactoryHelper;
import com.vectorprint.report.itext.style.StylingCondition;
import static com.vectorprint.report.itext.style.stylers.AbstractStyler.log;
import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

/**
 * Class for adding images (pdf (first page), tiff (first page) or other formats supported by the default awt toolkit)
 * to a report. Adding can be done through setup, {@link BaseReportGenerator#createAndAddElement(java.lang.Object, java.lang.Class, java.lang.String[])
 * } and {@link #style(com.itextpdf.text.Element, java.lang.Object) }, or by
 * {@link #draw(com.itextpdf.text.Rectangle, java.lang.String)}. This class initializes an image by retrieving data from
 * the URL parameter, or by trying to construct an URL from {@link #style(java.lang.Object, java.lang.Object) data}.
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class Image<DATATYPE> extends AbstractPositioning<DATATYPE> implements ImageLoaderAware {

   private ImageLoader imageLoader;
   public static final String URLPARAM = "url";
   public static final String SCALE = "scale";
   public static final String ROTATE = "rotate";
   public static final String PDF = "pdf";
   public static final String TIFF = "tiff";
   public static final String DOSTYLE = "dostyle";

   public Image(ImageLoader imageLoader, LayerManager layerManager, Document document, PdfWriter writer, EnhancedMap settings) throws VectorPrintException {
      StylerFactoryHelper.initStylingObject(this, writer, document, imageLoader, layerManager, settings);
      initParams();
   }

   private void initParams() {
      addParameter(new BooleanParameter(PDF, "is image a pdf"),Image.class);
      addParameter(new BooleanParameter(TIFF, "is image a tiff"),Image.class);
      addParameter(new URLParameter(URLPARAM, "url"),Image.class);
      addParameter(new com.vectorprint.configuration.parameters.FloatParameter(SCALE, "scales your image (percentage)").setDefault(100f),Image.class);
      addParameter(new com.vectorprint.configuration.parameters.FloatParameter(ROTATE, "rotates your image (degrees)"),Image.class);
      addParameter(new PasswordParameter(DocumentSettings.PASSWORD, "password for a pdf"),Image.class);
      addParameter(new BooleanParameter(DOSTYLE, "when true be part of regular styling"),Image.class);
   }

   public Image() {
      initParams();
   }

   /**
    * Calls {@link #createImage(com.itextpdf.text.pdf.PdfContentByte, java.lang.Object, float) }, {@link #applySettings(com.itextpdf.text.Image) },
    * {@link com.itextpdf.text.Image#setAbsolutePosition(float, float) } and 
    * {@link #addToCanvas(java.lang.Float[], com.itextpdf.text.Image, com.itextpdf.text.pdf.PdfContentByte) }.
    *
    * @param canvas
    * @param x
    * @param y
    * @param width
    * @param height
    * @param genericTag the value of genericTag
    * @throws VectorPrintException
    */
   @Override
   protected final void draw(PdfContentByte canvas, float x, float y, float width, float height, String genericTag) throws VectorPrintException {
      com.itextpdf.text.Image img;
      try {
         img = createImage(canvas, getData(), getValue((isDrawShadow()) ? SHADOWOPACITY : OPACITY, Float.class));
         applySettings(img);
      } catch (BadElementException ex) {
         throw new VectorPrintException(ex);
      }
      img.setAbsolutePosition(x, y);
      try {

         addToCanvas(getValue(TRANSFORM, Float[].class), img, canvas);

      } catch (DocumentException ex) {
         throw new VectorPrintRuntimeException(ex);
      }
   }

   /**
    * Adds an image to a canvas applying the transform if it is not null. Calls {@link DebugHelper#debugAnnotation(com.itextpdf.text.Rectangle, java.lang.String, com.itextpdf.text.pdf.PdfWriter)
    * }.
    *
    * @param tf may be null, the transform matrix to apply to the image
    * @param img
    * @param canvas
    * @throws DocumentException
    */
   public void addToCanvas(Float[] tf, com.itextpdf.text.Image img, PdfContentByte canvas) throws DocumentException {
      if (tf == null) {
         canvas.addImage(img);
      } else {
         canvas.addImage(img,
             img.getWidth() * tf[TRANSFORMMATRIX.SCALEX.getIndex()],
             tf[TRANSFORMMATRIX.SHEARX.getIndex()],
             tf[TRANSFORMMATRIX.SHEARY.getIndex()],
             img.getHeight() * tf[TRANSFORMMATRIX.SCALEY.getIndex()],
             img.getAbsoluteX() + tf[TRANSFORMMATRIX.TRANSLATEX.getIndex()],
             img.getAbsoluteY() + tf[TRANSFORMMATRIX.TRANSLATEY.getIndex()]);
      }
      if (getSettings().getBooleanProperty(ReportConstants.DEBUG, Boolean.FALSE) && !isDrawShadow()) {
         if (tf == null) {
            DebugHelper.debugAnnotation(
                new Rectangle(img.getAbsoluteX(), img.getAbsoluteY(), img.getAbsoluteX() + img.getWidth(), img.getAbsoluteY() + img.getHeight()), getStyleClass(), getWriter());
         } else {
            DebugHelper.debugAnnotation(
                new Rectangle(
                    img.getAbsoluteX() + tf[TRANSFORMMATRIX.TRANSLATEX.getIndex()],
                    img.getAbsoluteY() + tf[TRANSFORMMATRIX.TRANSLATEY.getIndex()],
                    img.getAbsoluteX() + tf[TRANSFORMMATRIX.TRANSLATEX.getIndex()] + img.getWidth() * tf[TRANSFORMMATRIX.SCALEX.getIndex()],
                    img.getAbsoluteY() + tf[TRANSFORMMATRIX.TRANSLATEY.getIndex()] + img.getHeight() * tf[TRANSFORMMATRIX.SCALEY.getIndex()]
                ), getStyleClass(), getWriter());
         }
      }
   }

   /**
    * Calls {@link #initURL(String) }, {@link #createImage(com.itextpdf.text.pdf.PdfContentByte, java.lang.Object, float) },
    * {@link #applySettings(com.itextpdf.text.Image) . Calls {@link VectorPrintDocument#addHook(com.vectorprint.report.itext.VectorPrintDocument.AddElementHook) } for
    * drawing image shadow and for drawing near this image.
    *
    * @param <E>
    * @param element
    * @param data when null use {@link #getData() }
    * @return
    * @throws VectorPrintException
    */
   @Override
   public final <E> E style(E element, Object data) throws VectorPrintException {
      initURL(String.valueOf((data != null) ? convert(data) : getData()));
      try {
         /*
          always call createImage when styling, subclasses may do their own document writing etc. in createImage
          */
         com.itextpdf.text.Image img = createImage(getWriter().getDirectContent(), (data != null) ? convert(data) : getData(), getValue(OPACITY, Float.class));
         if (data!=null) {
            setData(convert(data));
         }
         applySettings(img);
         VectorPrintDocument document = (VectorPrintDocument) getDocument();
         if (getValue(SHADOW, Boolean.class)) {
            // draw a shadow, but we do not know our position
            document.addHook(new VectorPrintDocument.AddElementHook(VectorPrintDocument.AddElementHook.INTENTION.PRINTIMAGESHADOW, img, this, getStyleClass()));
         }
         document.addHook(new VectorPrintDocument.AddElementHook(VectorPrintDocument.AddElementHook.INTENTION.DRAWNEARIMAGE, img, this, getStyleClass()));
         if (element != null) {
            // if we got here with an element, proceed no further, this element should finaly be added to the document
            return element;
         }

         return (E) img;
      } catch (BadElementException ex) {
         throw new VectorPrintException(ex);
      }
   }

   @Override
   public boolean creates() {
      return true;
   }

   private final ImageProcessorImpl imp = new ImageProcessorImpl();

   /**
    * when {@link #getUrl() } is null try to convert the data argument to a URL and call {@link #setUrl(java.net.URL) }.
    *
    * @param data
    * @return true when a url was successfully constructed from the argument
    */
   protected boolean initURL(String data) {
      if (getUrl() == null) {
         if (log.isLoggable(Level.FINE)) {
            log.fine(String.format("url parameter is null, trying to construct one from %s", data));
         }
         try {
            setUrl(new URL(data));
            return true;
         } catch (MalformedURLException ex) {
            if (log.isLoggable(Level.WARNING)) {
               log.warning(String.format("no url: %s", data));
            }
         }
      }
      return false;
   }

   /**
    * This implementation downloads an image from the URL taken from {@link #getUrl() }, which may be initialized by
    * {@link #initURL(java.lang.Object) }.
    *
    * @param canvas
    * @param data
    * @param opacity the value of opacity
    * @throws VectorPrintException
    * @throws BadElementException
    * @return the com.itextpdf.text.Image
    */
   protected com.itextpdf.text.Image createImage(PdfContentByte canvas, DATATYPE data, float opacity) throws VectorPrintException, BadElementException {
      initURL(String.valueOf((data != null) ? convert(data) : getData()));
      com.itextpdf.text.Image img = null;
      if (isPdf()) {
         imageLoader.loadPdf(getUrl(), getWriter(), getValue(DocumentSettings.PASSWORD, byte[].class), imp, 1);
         img = imp.getImage();
      } else if (getValue(TIFF, Boolean.class)) {
         imageLoader.loadTiff(getUrl(), imp, 1);
         img = imp.getImage();
      } else {
         img = imageLoader.loadImage(getUrl(), opacity);
      }
      return img;
   }

   /**
    * apply settings according to the capabilities of this Image Styler
    *
    * @param img
    */
   protected void applySettings(com.itextpdf.text.Image img) {
      if (img==null) {
         return;
      }
      img.scalePercent(getScale());
      img.setRotationDegrees(getValue(Image.ROTATE, Float.class));
      if (getSettings().getBooleanProperty(ReportConstants.DEBUG, Boolean.FALSE)) {
         DebugHelper.debugImage(getWriter().getDirectContent(), img, getSettings().getColorProperty("debugcolor", Color.MAGENTA), getStyleClass(),
             " (" + Image.class.getSimpleName() + " init)",
             getSettings(), getLayerManager(), (VectorPrintDocument) getDocument());
      }
   }

   @Override
   public void setImageLoader(ImageLoader imageLoader) {
      this.imageLoader = imageLoader;
   }

   public URL getUrl() {
      return getValue(URLPARAM, URL.class);
   }

   public void setUrl(URL url) {
      setValue(URLPARAM, url);
   }

   public float getScale() {
      return getValue(SCALE, Float.class);
   }

   public void setScale(float scale) {
      setValue(SCALE, scale);
   }

   public boolean isPdf() {
      return getValue(PDF, Boolean.class);
   }

   public void setPdf(boolean pdf) {
      setValue(PDF, pdf);
   }

   public float getRotate() {
      return getValue(ROTATE, Float.class);
   }

   public void setRotate(float rotate) {
      setValue(ROTATE, rotate);
   }

   @Override
   public String getHelp() {
      return "Draw an image at a position or near text or an image. " + super.getHelp();
   }

   protected ImageLoader getImageLoader() {
      return imageLoader;
   }

   /**
    * when true and {@link StylingCondition}s allow styling {@link #style(com.itextpdf.text.Element, java.lang.Object) }
    * will be called and an image will be returned by it.
    *
    * @return
    */
   public Boolean doStyle() {
      return getValue(DOSTYLE, Boolean.class);
   }

   public void setDoStyle(Boolean doStyle) {
      setValue(DOSTYLE, doStyle);
   }

   /**
    * calls {@link #shouldCallStyle() } and the super
    *
    * @param data
    * @param element the value of element
    * @return
    */
   /**
    * calls {@link #doStyle()} and the super
    *
    * @param data
    * @param element the value of element
    * @return
    */
   @Override
   public boolean shouldStyle(Object data, Object element) {
      if (!doStyle()) {
         log.warning(String.format("not styling becasue parameter %s is false. %s, key: %s", DOSTYLE, getClass().getName(), getStyleClass()));
      }
      return doStyle() && super.shouldStyle(data, element);
   }

}
