/** Runner.java
 * 
 * Copyright 2019 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.filteredpush.qc.date;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.api.result.NumericalValue;
import org.datakurator.ffdq.model.ResultState;
import org.joda.time.Instant;
import org.joda.time.Interval;

/**
 * Selfstanding execution of event_date_qc functionality.  Can run TG2 Date related tests on flat DarwinCore 
 * or can attempt to interpret a file of verbatimEventDates as eventDates.
 * 
 * @author mole
 *
 */
public class Runner {
	private static final Log logger = LogFactory.getLog(Runner.class);

	/**
	 * Execute Runner from the command line.
	 * 
	 * @param args -e verbatimDates or -e runTests with -f {filename} and for verbatimDates -m or -a.
	 */
	public static void main(String[] args) {
		Options options = new Options();
		Option opte = new Option("e", "execute", true, "Action to execute (verbatimDates,runTests)");
		opte.setRequired(true);
		options.addOption(opte);
		options.addRequiredOption("f", null, true, "Input file containing dates or darwin core data to test.");
		options.addOption("m", "(verbatimDates) show matched dates and their interpretations otheriwse lists non-matched lines");
		options.addOption("a","(verbatimDates) to show all lines, matched or not with their interpretations.");	
		options.addOption("l","limit",true,"Limit processing to the specified number of rows");

		try { 
			// Get option values
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);

			String execution = cmd.getOptionValue("e","runTests");
			if (execution.equals("verbatimDates")) {
				DateUtils.interpretDates(args);
			} else { 
				List<Method> methods = Arrays.asList(DwCEventTG2DQ.class.getDeclaredMethods());
				methods.get(0).isAnnotationPresent(Provides.class);
				methods.get(0).getAnnotation(Provides.class);

				String input = cmd.getOptionValue("f");
				String output = cmd.getOptionValue("out");
				String limitValue = cmd.getOptionValue("l");
				int limit = 0;
				if (limitValue!=null) { 
					try { 
						limit = Integer.parseInt(limitValue);
					} catch (NumberFormatException nfe) { 
						logger.error(nfe.getMessage());
					}
				}

				File inputFile = new File(input);
				if (!inputFile.exists()) {
					throw new FileNotFoundException("CSV input file not found: " + inputFile.getAbsolutePath());
				}

				int recordCount = 0;
				int skippedLineCount = 0;
				Long totalTimeSecs = 0l;
				Long totalTimeSecsPost = 0l;

				Reader reader = new InputStreamReader(new FileInputStream(inputFile));
				//Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
				CSVParser records = CSVFormat.TDF.withFirstRecordAsHeader().parse(reader);

				HashMap<String,Integer> counter = new HashMap<String,Integer>();
				HashMap<String,Integer> acounter = new HashMap<String,Integer>();
				HashMap<String,Integer> postcounter = new HashMap<String,Integer>();

				Class[] oneParam = new Class[1];
				oneParam[0] = String.class;
				Class[] twoParam = new Class[2];
				twoParam[0] = String.class;
				twoParam[1] = String.class;
				Class[] threeParam = new Class[3];
				threeParam[0] = String.class;
				threeParam[1] = String.class;
				threeParam[2] = String.class;
				Class[] fourParam = new Class[4];
				for (int i=0; i<4; i++) { fourParam[i] = String.class; }  
				Class[] sixParam = new Class[6];
				for (int i=0; i<6; i++) { sixParam[i] = String.class; }  
				Class[] sevenParam = new Class[7];
				for (int i=0; i<7; i++) { sevenParam[i] = String.class; }  

				Instant startTime = new Instant();
				System.out.println("Start time: " + startTime.toString());

				Iterator<CSVRecord> recordIterator = records.iterator();
				boolean hasNext = recordIterator.hasNext();
				while (hasNext && (limit==0 || recordCount < limit)) {
					CSVRecord record = null;
					try { 
						record = recordIterator.next();
						hasNext = recordIterator.hasNext();
					} catch (IllegalStateException linereadexception) {
						try { 
							record = recordIterator.next();
						} catch (Exception e) { 
							// skip
						}
						// skip line
						skippedLineCount++;
						continue;
					}

					String eventDate = "";
					String day = "";
					String month = "";
					String year = "";
					String startDayOfYear = "";
					String endDayOfYear = "";
					String verbatimEventDate = "";
					String dateIdentified = "";
					try { 
						eventDate = record.get("eventDate");
						day = record.get("day");
						month = record.get("month");
						year = record.get("year");
						startDayOfYear = record.get("startDayOfYear");
						endDayOfYear = record.get("endDayOfYear");
						verbatimEventDate = record.get("verbatimEventDate");
						dateIdentified = record.get("dateIdentified");
					} catch (IllegalArgumentException valueReadEx) { 
						logger.debug(valueReadEx.getMessage());
					}

					DQResponse response = null;
					DQResponse<NumericalValue> measureResponse = null;

					measureResponse = DwCEventTG2DQ.measureEventdatePrecisioninseconds(eventDate); 
					if (measureResponse.getResultState().equals(ResultState.RUN_HAS_RESULT)) { 
						totalTimeSecs = totalTimeSecs + measureResponse.getValue().getObject().longValue();
					}
					{
						String method = "VALIDATION_DATEIDENTIFIED_NOTSTANDARD";
						response = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
						
						method = "VALIDATION_DATEIDENTIFIED_OUTOFRANGE";
						response = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);

						method = "VALIDATION_EVENT_EMPTY";
						response = DwCEventTG2DQ.validationEventEmpty(startDayOfYear,eventDate,year, verbatimEventDate,month,day, endDayOfYear); 
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
						
						method = "VALIDATION_EVENT_INCONSISTENT";
						response = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year,month,day,endDayOfYear);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
						
						method = "VALIDATION_EVENTDATE_EMPTY";
						response = DwCEventDQ.validationEventdateEmpty(eventDate);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					              	
						method = "VALIDATION_EVENTDATE_NOTSTANDARD";
						response = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					            	
						method = "VALIDATION_EVENTDATE_OUTOFRANGE";
						response = DwCEventDQ.validationEventdateOutofrange(eventDate);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					 
						method = "VALIDATION_DAY_NOTSTANDARD"; 
						response = DwCEventTG2DQ.validationDayNotstandard(day);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
						
						method = "VALIDATION_DAY_OUTOFRANGE"; 
						response = DwCEventTG2DQ.validationDayOutofrange(year,month,day);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					
						method = "VALIDATION_MONTH_NOTSTANDARD"; 
						response = DwCEventTG2DQ.validationMonthNotstandard(month);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					
						method = "VALIDATION_YEAR_EMPTY"; 
						response = DwCEventTG2DQ.validationYearEmpty(year);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					
						method = "VALIDATION_YEAR_OUTOFRANGE"; 
						response = DwCEventTG2DQ.validationYearOutofRange(year); 
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
						
						method = "VALIDATION_STARTDAYOFYEAR_OUTOFRANGE"; 
						response = DwCEventTG2DQ.validationStartdayofyearOutofrange(startDayOfYear, year);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					
						method = "VALIDATION_ENDDAYOFYEAR_OUTOFRANGE";
						response = DwCEventTG2DQ.validationEnddayofyearOutofrange(year,endDayOfYear);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					}

					// Amendments  ********************************************  
					{
						String method = "AMENDMENT_DATEIDENTIFIED_STANDARDIZED";
						response = DwCOtherDateDQ.amendmentDateidentifiedStandardized(dateIdentified);
						String name = method + " " + response.getResultState().getLabel();
						Integer current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.CHANGED)) { 
							dateIdentified = ((AmendmentValue)response.getValue()).getObject().get("dwc:dateIdentified");
						}
					                    
						method = "AMENDMENT_EVENTDATE_STANDARDIZED";
						response = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
						name = method + " " + response.getResultState().getLabel();
						current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.CHANGED)) { 
							eventDate = ((AmendmentValue)response.getValue()).getObject().get("dwc:eventDate");
						}
					                   
						method = "AMENDMENT_DAY_STANDARDIZED";
						response = DwCEventTG2DQ.amendmentDayStandardized(day);
						name = method + " " + response.getResultState().getLabel();
						current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.CHANGED)) { 
							day = ((AmendmentValue)response.getValue()).getObject().get("dwc:day");
						}
					                    
						method = "AMENDMENT_MONTH_STANDARDIZED";
						response = DwCEventTG2DQ.amendmentMonthStandardized(month);
						name = method + " " + response.getResultState().getLabel();
						current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.CHANGED)) { 
							month = ((AmendmentValue)response.getValue()).getObject().get("dwc:month");
						}
						
						method = "AMENDMENT_EVENTDATE_FROM_YEARMONTHDAY";
						response = DwCEventTG2DQ.amendmentEventdateFromYearmonthday(eventDate,year,month,day);
						name = method + " " + response.getResultState().getLabel();
						current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.FILLED_IN)) { 
							eventDate = ((AmendmentValue)response.getValue()).getObject().get("dwc:eventDate");
						}
					                    
						method = "AMENDMENT_EVENTDATE_FROM_YEARSTARTDAYOFYEARENDDAYOFYEAR";
						response = DwCEventTG2DQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate,startDayOfYear,year,endDayOfYear);
						name = method + " " + response.getResultState().getLabel();
						current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.FILLED_IN)) { 
							eventDate = ((AmendmentValue)response.getValue()).getObject().get("dwc:eventDate");
						}
					                   
						method = "AMENDMENT_EVENTDATE_FROM_VERBATIM";
						response = DwCEventTG2DQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);
						name = method + " " + response.getResultState().getLabel();
						current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.FILLED_IN)) { 
							eventDate = ((AmendmentValue)response.getValue()).getObject().get("dwc:eventDate");
						}
						
						method = "AMENDMENT_EVENT_FROM_EVENTDATE"; 
						response = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate,startDayOfYear,year,month,day,endDayOfYear);
						name = method + " " + response.getResultState().getLabel();
						current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.FILLED_IN)) { 
							if (((AmendmentValue)response.getValue()).getObject().get("dwc:day")!=null) { 
							   day = ((AmendmentValue)response.getValue()).getObject().get("dwc:day");
							}
							if (((AmendmentValue)response.getValue()).getObject().get("dwc:month")!=null) { 
								month = ((AmendmentValue)response.getValue()).getObject().get("dwc:month");
							}
							if (((AmendmentValue)response.getValue()).getObject().get("dwc:year")!=null) { 
								year = ((AmendmentValue)response.getValue()).getObject().get("dwc:year");
							}
							if (((AmendmentValue)response.getValue()).getObject().get("dwc:startDayOfYear")!=null) { 
								startDayOfYear = ((AmendmentValue)response.getValue()).getObject().get("dwc:startDayOfYear");
							}
							if (((AmendmentValue)response.getValue()).getObject().get("dwc:endDayOfYear")!=null) { 
								endDayOfYear = ((AmendmentValue)response.getValue()).getObject().get("dwc:endDayOfYear");
							}
						}

					}
					// repeat validations post amendment   ****************************

					measureResponse = DwCEventTG2DQ.measureEventdatePrecisioninseconds(eventDate); 
					if (measureResponse.getResultState().equals(ResultState.RUN_HAS_RESULT)) { 
						totalTimeSecsPost = totalTimeSecsPost + measureResponse.getValue().getObject().longValue();
					}

					{
						String method = "VALIDATION_DATEIDENTIFIED_NOTSTANDARD";
						response = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
						
						method = "VALIDATION_DATEIDENTIFIED_OUTOFRANGE";
						response = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);

						method = "VALIDATION_EVENT_EMPTY";
						response = DwCEventTG2DQ.validationEventEmpty(startDayOfYear,eventDate,year, verbatimEventDate,month,day, endDayOfYear); 
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
						
						method = "VALIDATION_EVENT_INCONSISTENT";
						response = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year,month,day,endDayOfYear);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
						
						method = "VALIDATION_EVENTDATE_EMPTY";
						response = DwCEventDQ.validationEventdateEmpty(eventDate);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					              	
						method = "VALIDATION_EVENTDATE_NOTSTANDARD";
						response = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					            	
						method = "VALIDATION_EVENTDATE_OUTOFRANGE";
						response = DwCEventDQ.validationEventdateOutofrange(eventDate);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					 
						method = "VALIDATION_DAY_NOTSTANDARD"; 
						response = DwCEventTG2DQ.validationDayNotstandard(day);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
						
						method = "VALIDATION_DAY_OUTOFRANGE"; 
						response = DwCEventTG2DQ.validationDayOutofrange(year,month,day);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					
						method = "VALIDATION_MONTH_NOTSTANDARD"; 
						response = DwCEventTG2DQ.validationMonthNotstandard(month);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					
						method = "VALIDATION_YEAR_EMPTY"; 
						response = DwCEventTG2DQ.validationYearEmpty(year);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					
						method = "VALIDATION_YEAR_OUTOFRANGE"; 
						response = DwCEventTG2DQ.validationYearOutofRange(year); 
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
						
						method = "VALIDATION_STARTDAYOFYEAR_OUTOFRANGE"; 
						response = DwCEventTG2DQ.validationStartdayofyearOutofrange(startDayOfYear, year);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					
						method = "VALIDATION_ENDDAYOFYEAR_OUTOFRANGE";
						response = DwCEventTG2DQ.validationEnddayofyearOutofrange(year,endDayOfYear);
						name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					}

					recordCount++;
					if (recordCount % 1000000 == 0) { 
						Instant nowTime = new Instant();
						Interval runtime = new Interval(startTime,nowTime);
						System.out.println("Rows processed: " + recordCount + " in " + (runtime.toDuration().getMillis()/100)/10d + "seconds.");
					}
				}

				Instant endTime = new Instant();
				Interval runtime = new Interval(startTime,endTime);
				System.out.println("End time: " + endTime.toString());
				System.out.println("Runtime: " + (runtime.toDuration().getMillis()/100)/10d + " seconds") ;

				System.out.println("Records examined:" + Integer.toString(recordCount));
				System.out.println("Lines skipped:" + Integer.toString(skippedLineCount));

				System.out.println("Pre-amendment phase");
				System.out.println("Measure: Mean Duration (seconds) of eventDate over MultiRecord: " + totalTimeSecs/(recordCount-skippedLineCount));
				Set<String> keys = counter.keySet();
				List<String> skeys = new ArrayList<String>();
				for (String s :  keys) { skeys.add(s); } 
				Collections.sort(skeys);
				Iterator<String> i = skeys.iterator();
				while (i.hasNext()) { 
					String key = i.next();
					System.out.println(key + ": " + counter.get(key).toString());
				}

				System.out.println("Amendment phase");
				keys = acounter.keySet();
				skeys = new ArrayList<String>();
				for (String s :  keys) { skeys.add(s); } 
				Collections.sort(skeys);
				i = skeys.iterator();
				while (i.hasNext()) { 
					String key = i.next();
					System.out.println(key + ": " + acounter.get(key).toString());
				}

				System.out.println("Post-amendment phase");
				System.out.println("Measure: Mean Duration (seconds) of eventDate over MultiRecord: " + totalTimeSecsPost/recordCount);
				keys = postcounter.keySet();
				skeys = new ArrayList<String>();
				for (String s :  keys) { skeys.add(s); } 
				Collections.sort(skeys);
				i = skeys.iterator();
				while (i.hasNext()) { 
					String key = i.next();
					if (postcounter.get(key)!=null) { 
						System.out.println(key + ": " + postcounter.get(key).toString());
					}
				}

			}
		} catch (ParseException e) {
			System.out.println("ERROR: " + e.getMessage() + "\n");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "java -jar TestUtil.jar", options);
		} catch (FileNotFoundException e1) {
			logger.error("File not found", e1);
			System.out.println("ERROR: File not found: " + e1.getMessage() + "\n");
		} catch (IOException e2) {
			logger.error("Error reading file", e2);
			System.out.println("ERROR: " + e2.getMessage() + "\n");
		}
	}
}
