/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vectorprint.report.running;

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

import com.vectorprint.VectorPrintException;
import com.vectorprint.configuration.Settings;
import com.vectorprint.configuration.binding.parameters.ParameterizableBindingFactoryImpl;
import com.vectorprint.configuration.decoration.FindableProperties;
import com.vectorprint.configuration.parser.ParameterizableParserImpl;
import com.vectorprint.configuration.parser.ParseException;
import static com.vectorprint.report.ReportConstants.*;
import com.vectorprint.report.data.ReportDataHolder;
import com.vectorprint.report.itext.style.stylers.ChartBindingHelper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class JFreeChartReportBuilderTest {

   private static ThreadSafeReportBuilder< ReportDataHolder> instance;
//   private static 
   public static final String TARGET = "target" + File.separator;

   public JFreeChartReportBuilderTest() throws IOException {
   }

   @BeforeClass
   public static void setUpClass() throws IOException, VectorPrintException {
      Logger.getLogger(Settings.class.getName()).setLevel(Level.FINE);
   }

   private static void init(boolean allowEmpties) throws IOException, VectorPrintException, ParseException {
      FindableProperties.clearStaticReferences();
      instance = new ThreadSafeReportBuilder("src/test/resources/config",
          ThreadSafeReportBuilder.DEFAULTPROPERTYURLS.toArray(new String[ThreadSafeReportBuilder.DEFAULTPROPERTYURLS.size()]), allowEmpties, true);
      ParameterizableBindingFactoryImpl.getFactory(ParameterizableBindingFactoryImpl.PARAMPARSERCLASS, ParameterizableBindingFactoryImpl.PARAMSERIALIZERCLASS, new ChartBindingHelper(), true);
   }

   @AfterClass
   public static void tearDownClass() {
   }

   @Before
   public void setUp() throws IOException, VectorPrintException, ParseException {
      init(true);
      TestableReportGenerator.setDidCreate(false);
      instance.getSettings().remove(VERSION);
      instance.getSettings().remove(HELP);
   }

   @After
   public void tearDown() {
   }


   @Test
   public void testjFreeChart() throws Exception {
      instance.buildReport(new String[]{"output="+ TARGET+"testjFreeChart.pdf"});
      assertTrue(TestableReportGenerator.isDidCreate());
   }

   @Test
   public void testjFreeChartDebug() throws Exception {
      instance.buildReport(new String[]{"output="+ TARGET+"testjFreeChartDebug.pdf\ndebug=true"});
      assertTrue(TestableReportGenerator.isDidCreate());
   }

}
