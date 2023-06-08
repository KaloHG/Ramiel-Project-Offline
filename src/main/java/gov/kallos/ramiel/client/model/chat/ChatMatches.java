package gov.kallos.ramiel.client.model.chat;

import gov.kallos.ramiel.client.model.snitch.SnitchAlert;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Totally didn't steal this from gjum btw
 */
public class ChatMatches {
    public static final Pattern engagedInCombatPattern = Pattern.compile((String) "^You have engaged in combat with ([A-Za-z0-9_]{2,17}).*");
    public static final Pattern noLongerInCombatPattern = Pattern.compile((String) "^You are no longer in combat.*");
    public static final Pattern otherKillPattern = Pattern.compile((String) "^([A-Za-z0-9_]{2,17}) was killed by ([A-Za-z0-9_]{2,17})(?: (?:with )+(.+))?.*");
    public static final Pattern selfPearledPattern = Pattern.compile((String) "^You've been bound to a pearl by ([A-Za-z0-9_]{2,17}).*");
    public static final Pattern pearledOtherPattern = Pattern.compile((String) "^You've bound ([A-Za-z0-9_]{2,17}) to a.*[Pp]earl.*");
    public static final Pattern otherPearledPattern = Pattern.compile((String) "^([A-Za-z0-9_]{2,17}) was bound to a pearl by ([A-Za-z0-9_]{2,17}).*");
    public static final Pattern selfFreedPattern = Pattern.compile((String) "^You've been freed.*");
    public static final Pattern freedOtherPattern = Pattern.compile((String) "^You freed ([A-Za-z0-9_]{2,17}).*");
    public static final Pattern endPearledPattern = Pattern.compile((String) "^You've been imprisoned in the end by ([A-Za-z0-9_]{2,17}).*");

    static Pattern alertPattern = Pattern.compile((String) "^(Enter|Login|Logout) +([A-Za-z0-9_]{3,17}) +(.+[^ ]) +\\[(?:([A-Za-z][^ ]+),? )?([-0-9]+),? ([-0-9]+),? ([-0-9]+)\\].*");
    static Pattern hoverPattern = Pattern.compile((String) "Location: (?:\\(?([^\\n)]+)\\)? )?\\[([-0-9]+),? ([-0-9]+),? ([-0-9]+)\\] *\\nName: ([^\\n]+) *\\nGroup: ([^ ]+).*", (int) 8);

    @Nullable
    public static SnitchAlert getSnitchAlertFromChat(Text message, String world) {
        String hoverText;
        Matcher hoverMatch;
        String text = message.getString().replaceAll("\u00a7.", "");
        Matcher textMatch = alertPattern.matcher((CharSequence) text);
        if (!textMatch.matches()) {
            return null;
        }
        long ts = System.currentTimeMillis();
        String action = textMatch.group(1);
        String accountName = textMatch.group(2);
        String snitchName = textMatch.group(3);
        world = textMatch.group(4) == null ? world : textMatch.group(4);
        int x = Integer.parseInt((String) textMatch.group(5));
        int y = Integer.parseInt((String) textMatch.group(6));
        int z = Integer.parseInt((String) textMatch.group(7));
        String group = null;
        HoverEvent hoverEvent = ((Text) message.getSiblings().get(0)).getStyle().getHoverEvent();
        if (hoverEvent != null && hoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT && (hoverMatch = hoverPattern.matcher((CharSequence) (hoverText = ((Text) hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT)).getString().replaceAll("\u00a7.", "")))).matches()) {
            world = hoverMatch.group(1) == null ? world : hoverMatch.group(1);
            snitchName = hoverMatch.group(5);
            group = hoverMatch.group(6);
        }
        UUID uuid = null;
        return new SnitchAlert(world, x, y, z, ts, uuid, accountName, action, group, snitchName);
    }
}
