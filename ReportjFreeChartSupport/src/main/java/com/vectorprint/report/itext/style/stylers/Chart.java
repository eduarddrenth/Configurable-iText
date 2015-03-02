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
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.vectorprint.VectorPrintException;
import com.vectorprint.VectorPrintRuntimeException;
import com.vectorprint.configuration.EnhancedMap;
import com.vectorprint.configuration.annotation.SettingsAnnotationProcessor;
import com.vectorprint.configuration.annotation.SettingsAnnotationProcessorImpl;
import com.vectorprint.configuration.parameters.BooleanParameter;
import com.vectorprint.configuration.parameters.Parameter;
import com.vectorprint.configuration.parameters.StringParameter;
import com.vectorprint.report.itext.ImageLoader;
import com.vectorprint.report.itext.LayerManager;
import com.vectorprint.report.itext.style.parameters.FloatParameter;
import static com.vectorprint.report.itext.style.stylers.Chart.THEMEBUILDER;
import com.vectorprint.report.jfree.ChartBuilder;
import com.vectorprint.report.jfree.ChartThemeBuilder;
import com.vectorprint.report.jfree.ItextChartHelper;
import java.util.Arrays;

import static com.vectorprint.report.jfree.ChartBuilder.CHARTTYPE;
import static com.vectorprint.report.itext.style.stylers.DocumentSettings.WIDTH;
import java.util.Map;
import java.util.Observable;
import org.jfree.data.general.Dataset;

/**
 * printing jFree charts as images. If you want theme your charts you should implemement {@link ChartThemeBuilder}, give
 * it a no argument constructor and pass its classname as {@link #THEMEBUILDER parameter}.
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class Chart extends com.vectorprint.report.itext.style.stylers.Image<Dataset> {

   public static final String CAT_LABEL = "catLabel";
   public static final String VAL_LABEL = "valLabel";
   public static final String TITLE = "title";
   public static final String VERTICAL = "vertical";
   public static final String LEGEND = "legend";
   public static final String HEIGHT = "height";
   public static final String TYPE = "type";
   public static final String THEMEBUILDER = "themebuilder";
   private static final SettingsAnnotationProcessor SETTINGS_ANNOTATION_PROCESSOR = new SettingsAnnotationProcessorImpl();

   public Chart() {
      super();
      initParams();
   }

   private void initParams() {
      addParameter(new StringParameter(CAT_LABEL, "label for data categories").setDefault(CAT_LABEL));
      addParameter(new StringParameter(TITLE, "label for data categories").setDefault(TITLE));
      addParameter(new StringParameter(VAL_LABEL, "label for data categories").setDefault(VAL_LABEL));
      addParameter(new BooleanParameter(VERTICAL, "vertical orientation of chart"));
      addParameter(new BooleanParameter(LEGEND, "show legend or not").setDefault(Boolean.TRUE));
      addParameter(new FloatParameter(WIDTH, "width of the chart").setDefault(50f));
      addParameter(new FloatParameter(HEIGHT, "height of the chart").setDefault(50f));
      addParameter(new ChartTypeParameter(TYPE, "kind of chart: " + Arrays.asList(CHARTTYPE.values())));
      addParameter(new ChartThemeParameter(THEMEBUILDER, "classname of the themebuilder to use").
          setDefault(new DefaultChartThemeBuilder()));
   }

   public Chart(ImageLoader imageLoader, LayerManager layerManager, Document document, PdfWriter writer, EnhancedMap settings) throws VectorPrintException {
      super(imageLoader, layerManager, document, writer, settings);
      initParams();
   }

   /**
    * calls {@link ChartBuilder#ChartBuilder(com.vectorprint.report.jfree.ChartBuilder.CHARTTYPE, org.jfree.data.general.Dataset, java.lang.String, java.lang.String, java.lang.String, com.vectorprint.report.jfree.ChartThemeBuilder, com.vectorprint.configuration.EnhancedMap)}, {@link ChartBuilder#getChart()
    * } and {@link ItextChartHelper#getChartImage(org.jfree.chart.JFreeChart, com.itextpdf.text.pdf.PdfContentByte, float, float)
    * } to create an Image of a chart.
    *
    * @param canvas
    * @param data
    * @param opacity the value of opacity
    * @throws VectorPrintException
    * @throws BadElementException
    * @return the com.itextpdf.text.Image
    */
   @Override
   protected Image createImage(PdfContentByte canvas, Dataset data, float opacity) throws VectorPrintException, BadElementException {
      ChartBuilder cb = new ChartBuilder(getType(),
          data, getTitle(), getTitle(), getValLabel(), isVertical(), isLegend(), getValue(THEMEBUILDER, ChartThemeBuilder.class), getSettings());
      Image img = ItextChartHelper.getChartImage(cb.getChart(), canvas, getWidth(), getHeight(),opacity);
      applySettings(img);
      return img;
   }

   public CHARTTYPE getType() {
      return getValue(TYPE, CHARTTYPE.class);
   }

   public void setThemeBuilder(ChartThemeBuilder themeBuilder) {
      setValue(THEMEBUILDER, themeBuilder);
   }

   public String getCatLabel() {
      return getValue(CAT_LABEL, String.class);
   }

   public void setCatLabel(String catLabel) {
      setValue(CAT_LABEL, catLabel);
   }

   public String getValLabel() {
      return getValue(VAL_LABEL, String.class);
   }

   public void setValLabel(String valLabel) {
      setValue(VAL_LABEL, valLabel);
   }

   public String getTitle() {
      return getValue(TITLE, String.class);
   }

   public void setTitle(String title) {
      setValue(TITLE, title);
   }

   public boolean isVertical() {
      return getValue(VERTICAL, Boolean.class);
   }

   public void setVertical(boolean vertical) {
      setValue(VERTICAL, vertical);
   }

   public boolean isLegend() {
      return getValue(LEGEND, Boolean.class);
   }

   public void setLegend(boolean legend) {
      setValue(LEGEND, legend);
   }

   public float getWidth() {
      return getValue(WIDTH, Float.class);
   }

   public void setWidth(float width) {
      setValue(WIDTH, width);
   }

   public float getHeight() {
      return getValue(HEIGHT, Float.class);
   }

   public void setHeight(float height) {
      setValue(HEIGHT, height);
   }

   /**
    * Calls the super and {@link SettingsAnnotationProcessor#initSettings(java.lang.Object, com.vectorprint.configuration.EnhancedMap)
    * } on the {@link #THEMEBUILDER} parameter
    *
    * @param o
    * @param arg
    */
   @Override
   public void update(Observable o, Object arg) {
      super.update(o, arg);
      if (o instanceof Parameter) {
         Parameter p = (Parameter) o;
         if (THEMEBUILDER.equals(p.getKey())) {
            SETTINGS_ANNOTATION_PROCESSOR.initSettings(p.getValue(), getSettings());
         }
      }
   }

   /**
    * Calls the super and {@link SettingsAnnotationProcessor#initSettings(java.lang.Object, com.vectorprint.configuration.EnhancedMap)
    * } on the {@link #THEMEBUILDER} parameter
    *
    * @param args
    * @param settings
    */
   @Override
   public void setup(Map<String, String> args, Map<String, String> settings) {
      super.setup(args, settings);
      if (settings instanceof EnhancedMap) {
         SETTINGS_ANNOTATION_PROCESSOR.initSettings(getValue(THEMEBUILDER, ChartThemeBuilder.class), (EnhancedMap) settings);
      }
   }

   /**
    * When the argument is a {@link Dataset} just return it otherwise throw an exception. Override this
    * method to support conversion to {@link Dataset}.
    * @param s
    * @return 
    */
   @Override
   public Dataset convert(Object s) {
      if (s instanceof Dataset) {
         return (Dataset) s;
      } else {
         throw new VectorPrintRuntimeException(String.format(
             "override this method to convert %s (value: %s) to a Dataset", 
             (s!=null)?s.getClass().getName():null, String.valueOf(s)
         ));
      }
   }

}
