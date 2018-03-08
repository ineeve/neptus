/*
 * Copyright (c) 2004-2018 Universidade do Porto - Faculdade de Engenharia
 * Laboratório de Sistemas e Tecnologia Subaquática (LSTS)
 * All rights reserved.
 * Rua Dr. Roberto Frias s/n, sala I203, 4200-465 Porto, Portugal
 *
 * This file is part of Neptus, Command and Control Framework.
 *
 * Commercial Licence Usage
 * Licencees holding valid commercial Neptus licences may use this file
 * in accordance with the commercial licence agreement provided with the
 * Software or, alternatively, in accordance with the terms contained in a
 * written agreement between you and Universidade do Porto. For licensing
 * terms, conditions, and further information contact lsts@fe.up.pt.
 *
 * Modified European Union Public Licence - EUPL v.1.1 Usage
 * Alternatively, this file may be used under the terms of the Modified EUPL,
 * Version 1.1 only (the "Licence"), appearing in the file LICENSE.md
 * included in the packaging of this file. You may not use this work
 * except in compliance with the Licence. Unless required by applicable
 * law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific
 * language governing permissions and limitations at
 * https://github.com/LSTS/neptus/blob/develop/LICENSE.md
 * and http://ec.europa.eu/idabc/eupl.html.
 *
 * For more information please see <http://lsts.fe.up.pt/neptus>.
 *
 * Author: pdias
 * 08/03/2018
 */
package pt.lsts.neptus.plugins.noc.exporter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Vector;

import javax.swing.ProgressMonitor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import pt.lsts.imc.EntityParameter;
import pt.lsts.neptus.NeptusLog;
import pt.lsts.neptus.mp.Maneuver;
import pt.lsts.neptus.mp.ManeuverLocation;
import pt.lsts.neptus.mp.ManeuverLocation.Z_UNITS;
import pt.lsts.neptus.mp.maneuvers.Goto;
import pt.lsts.neptus.mp.maneuvers.LocatedManeuver;
import pt.lsts.neptus.mp.maneuvers.ManeuversUtil;
import pt.lsts.neptus.mp.maneuvers.StationKeeping;
import pt.lsts.neptus.plugins.noc.Utils;
import pt.lsts.neptus.types.mission.plan.IPlanFileExporter;
import pt.lsts.neptus.types.mission.plan.PlanType;
import pt.lsts.neptus.util.DateTimeUtil;
import pt.lsts.neptus.util.FileUtil;

/**
 * @author pdias
 *
 */
public class Autosub6000PlanExporter implements IPlanFileExporter {

    /** Tue Dec 15 13:34:50 2009 */
    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
    
    private static final String NEW_LINE = "\r\n";
    private static final String COMMENT_CHAR = "//";
    private static final String COMMENT_CHAR_WITH_SPACE = COMMENT_CHAR + " ";
    
    private static String START_WP_LABEL = "START_WAYPOINT";

    /**
     * 
     */
    public Autosub6000PlanExporter() {
        resetLocalData();
    }
    
    /* (non-Javadoc)
     * @see pt.lsts.neptus.types.mission.plan.IPlanFileExporter#getExporterName()
     */
    @Override
    public String getExporterName() {
        return "Autosub6000 Mission File";
    }

    /* (non-Javadoc)
     * @see pt.lsts.neptus.types.mission.plan.IPlanFileExporter#validExtensions()
     */
    @Override
    public String[] validExtensions() {
        return new String[] { "ms" };
    }
    
    private long wpCounter = 1;
    private LinkedHashMap<String, ManeuverLocation> wpVarArray = new LinkedHashMap<>();
    private String currentWP = null;
    private ManeuverLocation startLoc = null;
    private ManeuverLocation endLoc = null;
    
    /* (non-Javadoc)
     * @see pt.lsts.neptus.types.mission.plan.IPlanFileExporter#exportToFile(pt.lsts.neptus.types.mission.plan.PlanType, java.io.File, javax.swing.ProgressMonitor)
     */
    @Override
    public void exportToFile(PlanType plan, File out, ProgressMonitor monitor) throws Exception {
        resetLocalData();

        String genDateStr = getTimeStamp();
        
        String template = IOUtils.toString(FileUtil.getResourceAsStream("template.mis"));
        
        String genManListStr = processPlan(plan).toString();
        String genWPStartStr = Utils.locationAsStr(startLoc);
        String genWPEndStr = Utils.locationAsStr(endLoc);
        wpVarArray.remove(START_WP_LABEL);
        StringBuilder genWPVarsStrB = new StringBuilder(); 
        wpVarArray.keySet().stream().forEach(k -> genWPVarsStrB.append(k).append("=")
                .append(Utils.locationAsStr(wpVarArray.get(k))).append(NEW_LINE));

        template = replaceTokenWithKey(template, "WP-Start", genWPStartStr);
        template = replaceTokenWithKey(template, "WP-End", genWPEndStr); 
        
        template = replaceTokenWithKey(template, "WP-Vars", genWPVarsStrB.toString());
        
        template = replaceTokenWithKey(template, "MAN-LIST", genManListStr);
        
        FileUtils.write(out, template);
    }

    private void resetLocalData() {
        resetWpCounter();
        wpVarArray.clear();
        
        startLoc = null;
        endLoc = null;
        
    }

    private long resetWpCounter() {
        return wpCounter = 1;
    }

    private long nextWpCounter() {
        return wpCounter++;
    }

    /**
     * Tue Dec 15 13:34:50 2009
     * 
     * @return
     */
    private String getTimeStamp() {
        return dateFormatter.format(new Date());
    }
    /**
     * @param params
     * @param name
     * @return
     */
    private EntityParameter getParamWithName(Vector<EntityParameter> params, String name) {
        for (EntityParameter ep : params) {
            if (ep.getName().equalsIgnoreCase(name))
                return ep;
        }
        return null;
    }

    /**
     * @param original
     * @param key
     * @param replacement
     * @return
     */
    private String replaceTokenWithKey(String original, String key, String replacement) {
        return original.replaceAll("\\$\\{" + key + "\\}", replacement == null ? "" : replacement);
    }

    /**
     * @param txt
     * @return
     */
    private String getCommentLine(String... txt) {
        int cap = COMMENT_CHAR_WITH_SPACE.length() + NEW_LINE.length();
        for (String st : txt)
            cap += st.length();
        StringBuilder sb = new StringBuilder(cap);
        sb.append(COMMENT_CHAR_WITH_SPACE);
        for (String st : txt)
            sb.append(st);
        sb.append(NEW_LINE);
        return sb.toString();
    }
    
    /**
     * @param value
     * @return
     */
    private String formatReal(double value) {
        return String.format(Locale.US, "%f", value);
    }

    /**
     * @param value
     * @param decimalPlaces
     * @return
     */
    private String formatReal(double value, short decimalPlaces) {
        if (decimalPlaces < 0)
            return formatReal(value);
        
        return String.format(Locale.US, "%." + decimalPlaces + "f", value);
    }

    /**
     * @param value
     * @return
     */
    private String formatInteger(long value) {
        return "" + value;
    }

    private StringBuilder processPlan(PlanType plan) throws Exception {
        StringBuilder sb = new StringBuilder();
        
        for (Maneuver m : plan.getGraph().getManeuversSequence()) {
            double speedMS = ManeuversUtil.getSpeedMps(m);
            
            if (startLoc == null && m instanceof LocatedManeuver) {
                startLoc = ((LocatedManeuver) m).getStartLocation();
                currentWP = START_WP_LABEL;
                wpVarArray.put(currentWP, startLoc);
            }
            
            if (m instanceof StationKeeping) {
                if (Double.isNaN(speedMS))
                    continue;

                // processHeaderCommentAndPayloadForManeuver(sb, m);

                ManeuverLocation wp = ((StationKeeping) m).getManeuverLocation();
                wp.convertToAbsoluteLatLonDepth();
                
                sb.append(getCommandKeepPosition(m.getId(), wp.getLatitudeDegs(), wp.getLongitudeDegs(), wp.getZ(), wp.getZUnits(),
                        ((StationKeeping) m).getDuration()));
            }
            else if (m instanceof Goto) { // Careful with ordering because of extensions
                if (Double.isNaN(speedMS))
                    continue;

                // processHeaderCommentAndPayloadForManeuver(sb, m);

                ManeuverLocation wp = ((Goto) m).getManeuverLocation();
                wp.convertToAbsoluteLatLonDepth();
                sb.append(getCommandGoto(m.getId(), wp.getLatitudeDegs(), wp.getLongitudeDegs(), wp.getZ(), wp.getZUnits(),
                        speedMS));
            }
            else {
                NeptusLog.pub().warn(
                        String.format("Unsupported maneuver found \"%s\" in plan \"%s\".", m.getId(), plan.getId()));
            }
        }
        
        ArrayList<ManeuverLocation> al = new ArrayList<>(wpVarArray.values());
        endLoc = al.get(al.size() - 1);
        
       return sb;
    }

    /**
     * @param latDegs
     * @param lonDegs
     * @param depth
     * @param depthUnit
     * @return
     */
    private ManeuverLocation convertToManeuverLocation(double latDegs, double lonDegs, double depth,
            ManeuverLocation.Z_UNITS depthUnit) {
        ManeuverLocation curPos = new ManeuverLocation();
        curPos.setLatitudeDegs(latDegs);
        curPos.setLongitudeDegs(lonDegs);
        curPos.setZ(depth);
        curPos.setZUnits(depthUnit);
        return curPos;
    }
    
    private String getCommandGoto(String manName, double latDegs, double lonDegs, double depth, ManeuverLocation.Z_UNITS depthUnit,
            double speedMS) throws Exception {
        StringBuilder sb = new StringBuilder();
        ManeuverLocation thisPos = convertToManeuverLocation(latDegs, lonDegs, depth, depthUnit);
        String thisWP = manName  + "_" + 1;

        wpVarArray.put(thisWP, thisPos);

        long dur = Utils.calculateDurationMillis(wpVarArray.get(currentWP), thisPos, speedMS);
        String durationStr = Utils.durationAsStr((long) (dur * 1.2d));
        switch (depthUnit) {
            case ALTITUDE:
                // TrackAtAltitude(WPM1,WPM2,0:0:15:0,SCI_ALT) // estimated time (762.6m at 1.15ms) is 12 minutes, allow 3m for contingency, total is: 15mins
                sb.append(String.format("TrackAtAltitude(%s, %s, %s, %s)", currentWP, thisWP, durationStr, Double.toString(depth)));
                break;
            case DEPTH:
                sb.append(String.format("TrackAtDepth(%s, %s, %s, %s)", currentWP, thisWP, durationStr, Double.toString(depth)));
                break;
            case HEIGHT:
            case NONE:
            default:
                throw new Exception("Not supported z unit " + depthUnit);
        }
        sb.append(String.format(" // estimated time (%sm at %sm/s) is %s , allow %s for contingency, total is: %s", 
                Utils.calculateDistance(wpVarArray.get(currentWP), thisPos), 
                speedMS,
                DateTimeUtil.milliSecondsToFormatedString(dur, true),
                DateTimeUtil.milliSecondsToFormatedString((long) (dur * 0.2), true),
                DateTimeUtil.milliSecondsToFormatedString((long) (dur * 1.2), true)));
        sb.append(NEW_LINE);
        
        currentWP = thisWP;
        
        return sb.toString();
    }
    
    private String getCommandKeepPosition(String manName, double latDegs, double lonDegs, double depth,
            ManeuverLocation.Z_UNITS depthUnit, long timeSeconds) throws Exception {
        
        StringBuilder sb = new StringBuilder();
        ManeuverLocation thisPos = convertToManeuverLocation(latDegs, lonDegs, depth, depthUnit);
        String thisWP = manName + "_" + 1;

        wpVarArray.put(thisWP, thisPos);

        String durationStr = Utils.durationAsStr((long) (timeSeconds * 1E3));
        switch (depthUnit) {
            case ALTITUDE:
                // WaitAtAlt(START_WAYPOINT, 0:1:30:0, SCI_ALT)
                sb.append(String.format("WaitAtAlt(%s, %s, %s)", thisWP, durationStr, Double.toString(depth)));
                break;
            case DEPTH:
                sb.append(String.format("WaitAtDepth(%s, %s, %s)", thisWP, durationStr, Double.toString(depth)));
                break;
            case HEIGHT:
            case NONE:
            default:
                throw new Exception("Not supported z unit " + depthUnit);
        }
        sb.append(String.format(" // Wait at %sm %s", depth, depthUnit == Z_UNITS.ALTITUDE ? "altitude" : "depth"));
        sb.append(NEW_LINE);
        
        currentWP = thisWP;
        
        return sb.toString();
    }
}
