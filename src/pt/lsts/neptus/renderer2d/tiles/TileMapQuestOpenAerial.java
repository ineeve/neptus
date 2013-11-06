/*
 * Copyright (c) 2004-2013 Universidade do Porto - Faculdade de Engenharia
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
 * European Union Public Licence - EUPL v.1.1 Usage
 * Alternatively, this file may be used under the terms of the EUPL,
 * Version 1.1 only (the "Licence"), appearing in the file LICENCE.md
 * included in the packaging of this file. You may not use this work
 * except in compliance with the Licence. Unless required by applicable
 * law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific
 * language governing permissions and limitations at
 * https://www.lsts.pt/neptus/licence.
 *
 * For more information please see <http://lsts.fe.up.pt/neptus>.
 *
 * Author: Paulo Dias
 * 12/10/2011
 */
package pt.lsts.neptus.renderer2d.tiles;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import pt.lsts.neptus.plugins.MapTileProvider;

/**
 * @author pdias
 *
 */
@MapTileProvider(name = "MapQuest Open Aerial")
public class TileMapQuestOpenAerial extends TileHttpFetcher {

//  MapQuest tile
//
//  MapQuest-hosted map tiles
//  You are free to use the MapQuest map tiles in their existing applications or in your applications so long as you do the following:
//  You must always add the following attribution (including the hyperlinks) to any data, images and MapQuest-hosted map tiles: "Data, imagery and map information provided by MapQuest, Open Street Map <http://www.openstreetmap.org/> and contributors, CC-BY-SA <http://creativecommons.org/licenses/by-sa/2.0/> ."
//  Note: MapQuest is working on a proper/better MapQuest page that people can link to but MapQuest has no intention of forcing people to update work already done before that page is up.
//  Note: on http://developer.mapquest.com/web/products/open/map a different (and shorter) attribution code is posted:
//  Tiles Courtesy of <a href="http://www.mapquest.com/" target="_blank">MapQuest</a> <img src="http://developer.mapquest.com/content/osm/mq_logo.png">
//  Of course, only MapQuest is mentioned in it, and thus some separate OpenStreetMap credits are required.
//  Because this site uses open source mapping data, your use of the map tiles, data and images is subject to the licenses you see on the map tiles and your use must comply with this license. You cannot add a more restrictive license to the map tiles, data and images on the site or create derivative works with a more restrictive license.
//  If the application will get heavy usage (current defined as more than 4,000 tiles per second) please let MapQuest know in advance at open@mapquest.com. Please include the estimate of usage, so we can make sure that we can accommodate the load.
//  Before making an announcement (e.g., a press release, or something that is "all official-like") that relates to this site or the data, tiles or images on it, please contact MapQuest at open@mapquest.com with some notice because we'd like to hear about your efforts and if folks are going to get excited and hit the website, we'd like to have everything working at peak capacity.
//  All the information is provided "As-Is" and without any warranty of any kind. We are also under no obligation to provide any error corrections, updates, upgrades, bug fixes, etc. Since this is open source data, there will likely be errors and faults so please use at your own risk and if you see something that's not right, contribute to the Open Source Mapping community to correct it. Please also be aware that we have no obligation to provide support, although we may opt do so in our sole discretion if one of our developers gets the urge.
//  
//  Tile URLs
//  The tile URLs are very similar to regular OSM tiles, with only the front of the URL being different.
//  OpenStreetMap tile URL  http://a.tile.openstreetmap.org/8/126/87.png
//  MapQuest tile URL   http://otile1.mqcdn.com/tiles/1.0.0/osm/8/126/87.png
//  MapQuest Open Aerial tile URL   http://oatile1.mqcdn.com/naip/15/5240/12661.jpg
//  Just replace the "http://a.tile.openstreetmap.org" bit with "http://otile1.mqcdn.com/tiles/1.0.0/osm".
//  Note: MapQuest has 4 subdomains set up, otile1 to otile4, all pointing to the same CDN. Just like with OSM's a.tile to c.tile subdomains, these subdomains are provided to get around browser limitations on the number of simultaneous HTTP connections to each "host". Browser-based applications can thus request multiple tiles from multiple subdomains faster than from one subdomain.

    private static final long serialVersionUID = -6223894220961990667L;

    protected static String tileClassId = TileMapQuestOpenAerial.class.getSimpleName();

    private static Map<String, TileMapQuestOpenAerial> tilesMap = Collections.synchronizedMap(new HashMap<String, TileMapQuestOpenAerial>());

    private static final int MAX_LEVEL_OF_DETAIL = 18;

    public TileMapQuestOpenAerial(Integer levelOfDetail, Integer tileX, Integer tileY, BufferedImage image) throws Exception {
        super(levelOfDetail, tileX, tileY, image);
    }

    /**
     * @param id
     * @throws Exception
     */
    public TileMapQuestOpenAerial(String id) throws Exception {
        super(id);
    }

    public static int getMaxLevelOfDetail() {
        return MAX_LEVEL_OF_DETAIL;
    }

    /**
     * @return
     */
    @Override
    protected String createTileRequestURL() {
        // zoom/x/y
        // http://oatile1.mqcdn.com/naip/14/7801/6129.png
        int server = (int) (4 * rnd.nextFloat());
        String urlGet = "http://oatile" + server + "." + "mqcdn.com/naip/" + levelOfDetail + "/"
                + tileX + "/" + tileY + ".png";
        return urlGet;
    }

    /**
     * @return the tilesMap
     */
    @SuppressWarnings("unchecked")
    public static <T extends Tile> Map<String, T> getTilesMap() {
        return (Map<String, T>) tilesMap;
    }

    /**
     * 
     */
    public static void clearDiskCache() {
        Tile.clearDiskCache(tileClassId);
    }

    /**
     * @return 
     * 
     */
    public static <T extends Tile> Vector<T> loadCache() {
        return Tile.loadCache(tileClassId);
    }
}
