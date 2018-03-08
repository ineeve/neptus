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
import pt.lsts.neptus.util.MathMiscUtils;

import java.text.SimpleDateFormat;
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
        String fullStr = new SimpleDateFormat("d:h:m:s:S").format(new Date(timeMs));

        int daysIndex = fullStr.indexOf(":");

        // hh:ss:ms
        String hmsms = fullStr.substring( daysIndex + 1, fullStr.length());

        // just days
        String days = fullStr.substring(0, fullStr.indexOf(":"));
        return days + " " + hmsms;
    }

    public static double calculateDuration(ManeuverLocation p1, ManeuverLocation p2, double speed) {
        if(p1.getZUnits() != p2.getZUnits())
            return p1.getHorizontalDistanceInMeters(p2);

        double[] offsets = p1.getOffsetFrom(p2);
        offsets[2] = Math.abs(p2.getZ() - p1.getZ());

        double distance = Math.sqrt(offsets[0] * offsets[0] + offsets[1] * offsets[1] + offsets[2] * offsets[2]);
        return distance / speed;
    }
}