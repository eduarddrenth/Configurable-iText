package com.vectorprint.report.jfree;

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
//~--- non-JDK imports --------------------------------------------------------
import com.vectorprint.VectorPrintException;
import com.vectorprint.configuration.EnhancedMap;
import com.vectorprint.report.itext.style.stylers.Chart;

import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

//~--- JDK imports ------------------------------------------------------------
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class ChartBuilder {

   private boolean legend = true,
       tooltips = false,
       urls = false,
       vertical = false;
   private JFreeChart chart;
   private String title, categoryLabel, valueLabel;

   private EnhancedMap settings;

   public static enum CHARTTYPE {

      AREA, LINE, LINE3D, BAR, BAR3D, PIE, PIE3D, XYLINE, XYAREA, TIME
   }

   public static enum FILETYPE {

      JPEG, PNG
   }

   public ChartBuilder(CHARTTYPE type, Dataset data, ChartThemeBuilder themeBuilder, EnhancedMap settings)
       throws VectorPrintException {
      this(type, data, "", "", "", themeBuilder, settings);
   }

   public ChartBuilder(CHARTTYPE type, Dataset data, String title, String categoryLabel, String valueLabel,
       ChartThemeBuilder themeBuilder, EnhancedMap settings)
       throws VectorPrintException {
      this(type, data, title, categoryLabel, valueLabel, false, true, themeBuilder, settings);
   }

   /**
    * Bottleneck constructor, called from {@link Chart#initImage(com.itextpdf.text.pdf.PdfContentByte, org.jfree.data.general.Dataset) }.
    * @param type
    * @param data
    * @param title
    * @param categoryLabel
    * @param valueLabel
    * @param vertical
    * @param legend
    * @param themeBuilder
    * @param settings
    * @throws VectorPrintException 
    */
   public ChartBuilder(CHARTTYPE type, Dataset data, String title, String categoryLabel, String valueLabel,
       boolean vertical, boolean legend, ChartThemeBuilder themeBuilder, EnhancedMap settings)
       throws VectorPrintException {
      this.title = title;
      this.categoryLabel = categoryLabel;
      this.valueLabel = valueLabel;
      this.vertical = vertical;
      this.legend = legend;
      if (themeBuilder != null) {
         setTheme(themeBuilder.fromSettings(settings));
      }
      prepareChart(data, type);
      chart.setAntiAlias(true);
   }

   private PlotOrientation getOrientation() {
      return (vertical)
          ? PlotOrientation.VERTICAL
          : PlotOrientation.HORIZONTAL;
   }

   private void prepareChart(Dataset data, CHARTTYPE type) throws VectorPrintException {
      switch (type) {
         case AREA:
            chart = ChartFactory.createAreaChart(title, categoryLabel, valueLabel, null, getOrientation(), legend,
                tooltips, urls);
         case LINE:
            if (chart == null) {
               chart = ChartFactory.createLineChart(title, categoryLabel, valueLabel, null, getOrientation(), legend,
                   tooltips, urls);
            }
         case LINE3D:
            if (chart == null) {
               chart = ChartFactory.createLineChart3D(title, categoryLabel, valueLabel, null, getOrientation(), legend,
                   tooltips, urls);
            }
         case BAR:
            if (chart == null) {
               chart = ChartFactory.createBarChart(title, categoryLabel, valueLabel, null, getOrientation(), legend,
                   tooltips, urls);
            }
         case BAR3D:
            if (chart == null) {
               chart = ChartFactory.createBarChart3D(title, categoryLabel, valueLabel, null, getOrientation(), legend,
                   tooltips, urls);
            }

            if (data instanceof CategoryDataset) {
               CategoryDataset cd = (CategoryDataset) data;

               chart.getCategoryPlot().setDataset(cd);
            } else {
               throw new VectorPrintException("you should use CategoryDataset for this chart");
            }

            break;

         case PIE:
            chart = ChartFactory.createPieChart(title, null, legend, tooltips, urls);
         case PIE3D:
            if (chart == null) {
               chart = ChartFactory.createPieChart3D(title, null, legend, tooltips, urls);
            }

            if (data instanceof PieDataset) {
               PieDataset pd = (PieDataset) data;
               PiePlot pp = (PiePlot) chart.getPlot();

               pp.setDataset(pd);
            } else {
               throw new VectorPrintException("you should use PieDataset for this chart");
            }

            break;

         case XYLINE:
            chart = ChartFactory.createXYLineChart(title, categoryLabel, valueLabel, null, getOrientation(), legend,
                tooltips, urls);
         case XYAREA:
            if (chart == null) {
               chart = ChartFactory.createXYAreaChart(title, categoryLabel, valueLabel, null, getOrientation(), legend,
                   tooltips, urls);
            }

            if (data instanceof XYDataset) {
               XYDataset xy = (XYDataset) data;

               chart.getXYPlot().setDataset(xy);
            } else {
               throw new VectorPrintException("you should use XYDataset for this chart");
            }

            break;

         case TIME:
            chart = ChartFactory.createTimeSeriesChart(title, categoryLabel, valueLabel, null, legend, tooltips, urls);

            if (data instanceof TimeSeriesCollection) {
               TimeSeriesCollection xy = (TimeSeriesCollection) data;

               chart.getXYPlot().setDataset(xy);
            } else {
               throw new VectorPrintException("you should use TimeSeriesCollection for this chart");
            }

            break;

         default:
            throw new VectorPrintException("unsupported chart");
      }

      chart.setAntiAlias(true);
      chart.setTextAntiAlias(true);
   }

   public void setTheme(ChartTheme theme) {
      ChartFactory.setChartTheme(theme);
   }

   public JFreeChart getChart() {
      return chart;
   }

   public void addLineOverlay(CategoryDataset dataset) {
      if ((dataset != null) && (chart.getPlot() instanceof CategoryPlot)) {
         CategoryPlot plot = chart.getCategoryPlot();

         plot.setDataset(1, dataset);
      }
   }

   public ChartFrame show() {
      ChartFrame cf = new ChartFrame(chart.getTitle().getText(), chart);

      cf.pack();
      cf.setVisible(true);

      return cf;
   }

   public void save(OutputStream img, FILETYPE type, int width, int height) throws IOException {
      switch (type) {
         case JPEG:
            ChartUtilities.writeChartAsJPEG(img, chart, width, height);

            break;

         case PNG:
            ChartUtilities.writeChartAsPNG(img, chart, width, height);

            break;
      }
   }

   public String getCategoryLabel() {
      return categoryLabel;
   }

   public void setCategoryLabel(String categoryLabel) {
      this.categoryLabel = categoryLabel;
   }

   public boolean isLegend() {
      return legend;
   }

   public void setLegend(boolean legend) {
      this.legend = legend;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public boolean isTooltips() {
      return tooltips;
   }

   public void setTooltips(boolean tooltips) {
      this.tooltips = tooltips;
   }

   public boolean isUrls() {
      return urls;
   }

   public void setUrls(boolean urls) {
      this.urls = urls;
   }

   public String getValueLabel() {
      return valueLabel;
   }

   public void setValueLabel(String valueLabel) {
      this.valueLabel = valueLabel;
   }

   public boolean isVertical() {
      return vertical;
   }

   public void setVertical(boolean vertical) {
      this.vertical = vertical;
   }
}
