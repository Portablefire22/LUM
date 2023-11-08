package com.lvu;

import com.lvu.waypoint.WaypointRender;
import com.lvu.xray.render.XrayRender;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.BufferBuilder;

public class RenderManager implements WorldRenderEvents.End  {



    @Override
    public void onEnd(WorldRenderContext context) {
        if(MainClient.UtilityStatus.get("xray").equals("true")) {
            XrayRender.Render(context);
        }
        if (MainClient.UtilityStatus.get("waypoint").equals("true")) {
            WaypointRender.Render(context);
        }
    }
}
