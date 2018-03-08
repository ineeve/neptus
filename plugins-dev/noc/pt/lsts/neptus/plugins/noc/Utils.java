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
 * Version 1.1 only (the "Licence"), appearing in the file LICENCE.md
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
 * Author: Tiago Sá Marques
 * 8 MAR, 2018
 */
package pt.lsts.neptus.plugins.noc;

import pt.lsts.imc.def.ZUnits;
import pt.lsts.neptus.mp.Maneuver;
import pt.lsts.neptus.mp.ManeuverLocation;
import pt.lsts.neptus.types.coord.CoordinateUtil;
import pt.lsts.neptus.types.coord.LocationType;
import pt.lsts.neptus.util.DateTimeUtil;
import pt.lsts.neptus.util.MathMiscUtils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author tsm
 *
 */
public class Utils {

    /**
     * Given a LocationType returns a string representation
     * as: N/S:Degrees:Minutes,E/W:Degrees:Minutes
     * with 3 decimal places
     * */
    public static String locationAsStr(LocationType loc) {
        return locationAsStr(loc, 5);
    }

    /**
     * Given a LocationType returns a string representation
     * as: N/S:Degrees:Minutes,E/W:Degrees:Minutes
     * with the given number of decimal places
     * */
    public static String locationAsStr(LocationType loc, int nPlances) {
        loc = loc.getNewAbsoluteLatLonDepth();
        double lat[] = CoordinateUtil.decimalDegreesToDM(loc.getLatitudeDegs());
        double lon[] = CoordinateUtil.decimalDegreesToDM(loc.getLongitudeDegs());

        lat[0] = MathMiscUtils.round(lat[0], nPlances);
        lat[1] = MathMiscUtils.round(lat[1], nPlances);
        lon[0] = MathMiscUtils.round(lon[0], nPlances);
        lon[1] = MathMiscUtils.round(lon[1], nPlances);

        StringBuilder sb = new StringBuilder();

        sb.append((lat[0] > 0)? ("N:" + lat[0]) : ("S:" + Math.abs(lat[0])));
        sb.append(":" + lat[1]);
        sb.append(",");
        sb.append((lon[0] > 0)? ("E:" + lon[0]) : ("W:" + Math.abs(lon[0])));
        sb.append(":" + lon[1]);

        return sb.toString();
    }

    /**
     * Given a time in milliseconds returns a string
     * in the following format:
     *
     * days hours:minutes:seconds:millis
     * */
    public static String durationAsStr(long timeMs) {
        double timeSec = timeMs / 1000;

        long dy = (long) (timeSec/60.0/60.0/24.0);
        long hr = (long) ((timeSec/60.0/60.0)%24);
        long mi = (long) ((timeSec/60.0)%60.0);
        long sec = (long) (timeSec%60);
        long ms = (timeMs % 1000);


        return dy + " " + hr + ":" + mi + ":" + sec + ":" + ms;
    }

    public static long calculateDurationMillis(ManeuverLocation p1, ManeuverLocation p2, double speed) {
        double distance = calculateDistance(p1, p2);
        double duration = (distance / speed) * 1000;
        return (long) MathMiscUtils.round(duration, 0);
    }

    public static double calculateDistance(ManeuverLocation p1, ManeuverLocation p2) {
        double distance;

        if(p1.getZUnits() != p2.getZUnits())
            distance =  p1.getHorizontalDistanceInMeters(p2);
        else {
            double[] offsets = p1.getOffsetFrom(p2);
            offsets[2] = Math.abs(p2.getZ() - p1.getZ());

            distance = Math.sqrt(offsets[0] * offsets[0] + offsets[1] * offsets[1] + offsets[2] * offsets[2]);
        }

        return (long) MathMiscUtils.round(distance, 0);
    }

    public static void main(String[] args) {
        ManeuverLocation l1 = new ManeuverLocation(LocationType.FEUP);
        l1.setOffsetNorth(20);

        ManeuverLocation l2 = new ManeuverLocation(LocationType.FEUP);

        System.out.println(calculateDurationMillis(l2, l1, 1));
        System.out.println(durationAsStr(calculateDurationMillis(l2, l1, 1)));

        System.out.println(durationAsStr(2051));
    }
}