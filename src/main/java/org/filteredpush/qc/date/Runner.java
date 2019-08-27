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
				DwCEventTG2DQ tester = new DwCEventTG2DQ();
				List<Method> methods = Arrays.asList(tester.getClass().getDeclaredMethods());
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

					measureResponse = tester.measureEventdatePrecisioninseconds(eventDate); 
					if (measureResponse.getResultState().equals(ResultState.RUN_HAS_RESULT)) { 
						totalTimeSecs = totalTimeSecs + measureResponse.getValue().getObject().longValue();
					}

					try { 
						String method = tester.getClass().getMethod("validationDateidentifiedNotstandard",oneParam).getName();
						response = tester.validationDateidentifiedNotstandard(dateIdentified);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationDateidentifiedOutofrange",twoParam).getName();
						response = tester.validationDateidentifiedOutofrange(dateIdentified,eventDate);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}

					try { 
						String method = tester.getClass().getMethod("validationEventEmpty",sevenParam).getName();
						response = tester.validationEventEmpty(startDayOfYear,eventDate,year, verbatimEventDate,month,day, endDayOfYear); 
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationEventInconsistent",sixParam).getName();
						response = tester.validationEventInconsistent(startDayOfYear, eventDate, year,month,day,endDayOfYear);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationEventdateEmpty",oneParam).getName();
						response = tester.validationEventdateEmpty(eventDate);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}              	
					try { 
						String method = tester.getClass().getMethod("validationEventdateNotstandard",oneParam).getName();
						response = tester.validationEventdateNotstandard(eventDate);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}            	
					try { 
						String method = tester.getClass().getMethod("validationEventdateOutofrange",oneParam).getName();
						response = tester.validationEventdateOutofrange(eventDate);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}            	
					try { 
						String method = tester.getClass().getMethod("validationDayNotstandard",oneParam).getName();
						response = tester.validationDayNotstandard(day);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationDayOutofrange",threeParam).getName();
						response = tester.validationDayOutofrange(year,month,day);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationMonthNotstandard",oneParam).getName();
						response = tester.validationMonthNotstandard(month);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationYearEmpty",oneParam).getName();
						response = tester.validationYearEmpty(year);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationYearOutofRange",oneParam).getName();
						response = tester.validationYearOutofRange(year); 
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationStartdayofyearOutofrange",twoParam).getName();
						response = tester.validationStartdayofyearOutofrange(startDayOfYear, year);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationEnddayofyearOutofrange",twoParam).getName();
						response = tester.validationEnddayofyearOutofrange(year,endDayOfYear);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = counter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						counter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}

					// Amendments   
					try { 
						String method = tester.getClass().getMethod("amendmentDateidentifiedStandardized",oneParam).getName();
						response = tester.amendmentDateidentifiedStandardized(dateIdentified);
						String name = method + " " + response.getResultState().getLabel();
						Integer current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.CHANGED)) { 
							dateIdentified = ((AmendmentValue)response.getValue()).getObject().get("dwc:dateIdentified");
						}
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}                    
					try { 
						String method = tester.getClass().getMethod("amendmentEventdateStandardized",oneParam).getName();
						response = tester.amendmentEventdateStandardized(eventDate);
						String name = method + " " + response.getResultState().getLabel();
						Integer current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.CHANGED)) { 
							eventDate = ((AmendmentValue)response.getValue()).getObject().get("dwc:eventDate");
						}
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}                    
					try { 
						String method = tester.getClass().getMethod("amendmentDayStandardized",oneParam).getName();
						response = tester.amendmentDayStandardized(day);
						String name = method + " " + response.getResultState().getLabel();
						Integer current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.CHANGED)) { 
							day = ((AmendmentValue)response.getValue()).getObject().get("dwc:day");
						}
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}                    
					try { 
						String method = tester.getClass().getMethod("amendmentMonthStandardized",oneParam).getName();
						response = tester.amendmentMonthStandardized(month);
						String name = method + " " + response.getResultState().getLabel();
						Integer current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.CHANGED)) { 
							month = ((AmendmentValue)response.getValue()).getObject().get("dwc:month");
						}
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}                    
					try { 
						String method = tester.getClass().getMethod("amendmentEventdateFromYearmonthday",fourParam).getName();
						response = tester.amendmentEventdateFromYearmonthday(eventDate,year,month,day);
						String name = method + " " + response.getResultState().getLabel();
						Integer current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.FILLED_IN)) { 
							eventDate = ((AmendmentValue)response.getValue()).getObject().get("dwc:eventDate");
						}
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}                    
					try { 
						String method = tester.getClass().getMethod("amendmentEventdateFromYearstartdayofyearenddayofyear",fourParam).getName();
						response = tester.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate,startDayOfYear,year,endDayOfYear);
						String name = method + " " + response.getResultState().getLabel();
						Integer current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.FILLED_IN)) { 
							eventDate = ((AmendmentValue)response.getValue()).getObject().get("dwc:eventDate");
						}
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}                    
					try { 
						String method = tester.getClass().getMethod("amendmentEventdateFromVerbatim",twoParam).getName();
						response = tester.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);
						String name = method + " " + response.getResultState().getLabel();
						Integer current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.FILLED_IN)) { 
							eventDate = ((AmendmentValue)response.getValue()).getObject().get("dwc:eventDate");
						}
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}                    
					try { 
						String method = tester.getClass().getMethod("amendmentEventFromEventdate",sixParam).getName();
						response = tester.amendmentEventFromEventdate(eventDate,startDayOfYear,year,month,day,endDayOfYear);
						String name = method + " " + response.getResultState().getLabel();
						Integer current = acounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						acounter.put(name, current);
						if (response.getResultState().equals(ResultState.FILLED_IN)) { 
							day = ((AmendmentValue)response.getValue()).getObject().get("dwc:day");
							month = ((AmendmentValue)response.getValue()).getObject().get("dwc:month");
							year = ((AmendmentValue)response.getValue()).getObject().get("dwc:year");
							startDayOfYear = ((AmendmentValue)response.getValue()).getObject().get("dwc:startDayOfYear");
							endDayOfYear = ((AmendmentValue)response.getValue()).getObject().get("dwc:endDayOfYear");
						}
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}                    

					// repeat validations post amendment     

					measureResponse = tester.measureEventdatePrecisioninseconds(eventDate); 
					if (measureResponse.getResultState().equals(ResultState.RUN_HAS_RESULT)) { 
						totalTimeSecsPost = totalTimeSecsPost + measureResponse.getValue().getObject().longValue();
					}

					try { 
						String method = tester.getClass().getMethod("validationDateidentifiedNotstandard",oneParam).getName();
						response = tester.validationDateidentifiedNotstandard(dateIdentified);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationDateidentifiedOutofrange",twoParam).getName();
						response = tester.validationDateidentifiedOutofrange(dateIdentified,eventDate);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}

					try { 
						String method = tester.getClass().getMethod("validationEventEmpty",sevenParam).getName();
						response = tester.validationEventEmpty(startDayOfYear,eventDate,year, verbatimEventDate,month,day, endDayOfYear); 
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationEventInconsistent",sixParam).getName();
						response = tester.validationEventInconsistent(startDayOfYear, eventDate, year,month,day,endDayOfYear);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationEventdateEmpty",oneParam).getName();
						response = tester.validationEventdateEmpty(eventDate);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}              	
					try { 
						String method = tester.getClass().getMethod("validationEventdateNotstandard",oneParam).getName();
						response = tester.validationEventdateNotstandard(eventDate);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}            	
					try { 
						String method = tester.getClass().getMethod("validationEventdateOutofrange",oneParam).getName();
						response = tester.validationEventdateOutofrange(eventDate);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}            	
					try { 
						String method = tester.getClass().getMethod("validationDayNotstandard",oneParam).getName();
						response = tester.validationDayNotstandard(day);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationDayOutofrange",threeParam).getName();
						response = tester.validationDayOutofrange(year,month,day);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationMonthNotstandard",oneParam).getName();
						response = tester.validationMonthNotstandard(month);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationYearEmpty",oneParam).getName();
						response = tester.validationYearEmpty(year);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationYearOutofRange",oneParam).getName();
						response = tester.validationYearOutofRange(year); 
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationStartdayofyearOutofrange",twoParam).getName();
						response = tester.validationStartdayofyearOutofrange(startDayOfYear, year);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}
					try { 
						String method = tester.getClass().getMethod("validationEnddayofyearOutofrange",twoParam).getName();
						response = tester.validationEnddayofyearOutofrange(year,endDayOfYear);
						String name = method + " " + response.getResultState().getLabel() + " ";
						if (response.getValue()!=null) { name = name + response.getValue().getObject().toString(); }
						Integer current = postcounter.get(name);
						if (current==null) { current = 0; }
						current = current + 1;
						postcounter.put(name, current);
					} catch (NoSuchMethodException e) { 
						logger.error("test not found", e);
					}

					recordCount++;
				}

				Instant endTime = new Instant();
				Interval runtime = new Interval(startTime,endTime);
				System.out.println("End time: " + endTime.toString());
				System.out.println("Runtime: " + (runtime.toDuration().getMillis()/100)/10d + " seconds") ;

				System.out.println("Records examined:" + Integer.toString(recordCount));
				System.out.println("Lines skipped:" + Integer.toString(skippedLineCount));

				System.out.println("Pre-amendment phase");
				System.out.println("Measure: Mean Duration (seconds) of eventDate over MultiRecord: " + totalTimeSecs/recordCount);
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