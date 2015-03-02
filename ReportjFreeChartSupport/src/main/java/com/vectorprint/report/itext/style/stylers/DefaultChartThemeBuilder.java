/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vectorprint.report.itext.style.stylers;

import com.vectorprint.configuration.EnhancedMap;
import com.vectorprint.report.jfree.ChartThemeBuilder;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.StandardChartTheme;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class DefaultChartThemeBuilder implements ChartThemeBuilder {
   
   /**
    * returns a new instance of {@link StandardChartTheme}, does not use the settings to configure it.
    * @param settings
    * @return 
    */
   @Override
   public ChartTheme fromSettings(EnhancedMap settings) {
      return new StandardChartTheme("StandardChartTheme");
   }

}
