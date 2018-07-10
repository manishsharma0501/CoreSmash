package com.breakthecore;

import com.breakthecore.tilemap.TilemapTile;
import com.breakthecore.tiles.Matchable;
import com.breakthecore.tiles.TileContainer.Side;
import com.breakthecore.tiles.TileType;

import java.util.ArrayList;

public class Match3 {
    private ArrayList<TilemapTile> matched = new ArrayList<TilemapTile>();
    private ArrayList<TilemapTile> closed = new ArrayList<TilemapTile>();

    public ArrayList<TilemapTile> getColorMatchesFromTile(TilemapTile tile) {
        matched.clear();
        closed.clear();
        addSurroundingColorMatches(tile);
        return matched;
    }

    private void addSurroundingColorMatches(TilemapTile tmTile) {
        closed.add(tmTile);
        matched.add(tmTile);

        TilemapTile neighbour;
        //top_left
        neighbour = tmTile.getNeighbour(Side.TOP_LEFT);
        if (neighbour != null && !closed.contains(neighbour)) {
            if (isMatching(tmTile, neighbour)) {
                addSurroundingColorMatches(neighbour);
            }
        }

        //top_right
        neighbour = tmTile.getNeighbour(Side.TOP_RIGHT);
        if (neighbour != null && !closed.contains(neighbour)) {
            if (isMatching(tmTile, neighbour)) {
                addSurroundingColorMatches(neighbour);
            }
        }

        //right
        neighbour = tmTile.getNeighbour(Side.RIGHT);
        if (neighbour != null && !closed.contains(neighbour)) {
            if (isMatching(tmTile, neighbour)) {
                addSurroundingColorMatches(neighbour);
            }
        }

        //bottom_right
        neighbour = tmTile.getNeighbour(Side.BOTTOM_RIGHT);
        if (neighbour != null && !closed.contains(neighbour)) {
            if (isMatching(tmTile, neighbour)) {
                addSurroundingColorMatches(neighbour);
            }
        }

        //bottom_left
        neighbour = tmTile.getNeighbour(Side.BOTTOM_LEFT);
        if (neighbour != null && !closed.contains(neighbour)) {
            if (isMatching(tmTile, neighbour)) {
                addSurroundingColorMatches(neighbour);
            }
        }

        //left
        neighbour = tmTile.getNeighbour(Side.LEFT);
        if (neighbour != null && !closed.contains(neighbour)) {
            if (isMatching(tmTile, neighbour)) {
                addSurroundingColorMatches(neighbour);
            }
        }
    }

    private boolean isMatching(TilemapTile tmTile, TilemapTile neighbour) {
        return neighbour.getTile() instanceof Matchable &&
                (((Matchable) neighbour.getTile()).matchesWith(tmTile.getTileID()) || ((Matchable) tmTile.getTile()).matchesWith(neighbour.getTileID()));
    }
}
