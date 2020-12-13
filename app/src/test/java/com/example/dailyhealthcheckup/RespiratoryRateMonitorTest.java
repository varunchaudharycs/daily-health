package com.example.dailyhealthcheckup;

import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for RespiratoryRateCalculator with data collected from sampled accelerometer values
 * Observed output:
 * ZeroCrossing: 18
 * ZeroCrossing: 17
 * ZeroCrossing: 20
 * Respiratory rate = 12.0
 */
public class RespiratoryRateMonitorTest {

    List<float[]> values;

    protected void setUp() {

        float[] x = {-1.9106343f,-1.8851225f,-1.8788939f,-1.9113528f,-1.9499199f,-1.9963923f,-2.0114837f,-2.0005844f,-1.9887266f,-1.961059f,-1.8988962f,-1.8563766f,-1.8193665f,-1.7946928f,-1.7651087f,-1.7652283f,-1.7548082f,-1.7697799f,-1.7828354f,-1.7873869f,-1.80679f,-1.8422432f,-1.8681142f,-1.8670366f,-1.9063221f,-1.9359066f,-1.995075f,-1.9954342f,-1.9815404f,-1.9484828f,-1.8955425f,-1.8760192f,-1.8176893f,-1.786788f,-1.7842728f,-1.7972084f,-1.7570841f,-1.7751697f,-1.7739719f,-1.7663063f,-1.7887045f,-1.7876263f,-1.7943338f,-1.8164921f,-1.8627247f,-1.898896f,-1.9102745f,-1.9065619f,-1.8838046f,-1.8433212f,-1.8060715f,-1.7879854f,-1.7683427f,-1.7482207f,-1.7185167f,-1.725344f,-1.7399563f,-1.7510952f,-1.74858f,-1.7660669f,-1.757563f,-1.761755f,-1.767025f,-1.7829549f,-1.821283f,-1.8833256f,-1.9030883f,-1.9182997f,-1.9023699f,-1.8845234f,-1.8780556f,-1.8376919f,-1.8029575f,-1.8082273f,-1.7730141f,-1.7877461f,-1.7787632f,-1.7763675f,-1.7749302f,-1.7743315f,-1.7768466f,-1.8030771f,-1.8169708f,-1.8212827f,-1.840327f,-1.8683538f,-1.9068016f,-1.9410568f,-1.9706408f,-1.9604601f,-1.9566272f,-1.918659f,-1.8993753f,-1.8617662f,-1.8418839f,-1.8252351f,-1.8297869f,-1.8094251f,-1.8103834f,-1.8028376f,-1.8042748f,-1.8160127f,-1.8429619f,-1.8483517f,-1.8651202f,-1.8881166f,-1.9157845f,-1.9477638f,-1.9820194f,-1.9883674f,-1.993398f,-1.9828575f,-1.9773481f,-1.9272829f,-1.8972194f,-1.8754206f,-1.8443992f,-1.8336195f,-1.8161325f,-1.8146951f,-1.8161325f,-1.7918184f,-1.7942139f,-1.795771f,-1.8002026f,-1.810743f,-1.8054726f,-1.8066702f,-1.8323019f,-1.8398478f,-1.8345778f,-1.8502681f,-1.872426f,-1.9062023f,-1.937104f,-1.9701618f,-1.9632146f,-1.933391f,-1.9117123f,-1.8915901f,-1.8466749f,-1.8145752f,-1.8120601f,-1.8156532f,-1.7978071f,-1.7927767f,-1.7877461f,-1.7737324f,-1.7958908f,-1.7954117f,-1.7988853f,-1.8006815f,-1.7960106f,-1.7980467f,-1.8255947f,-1.8475132f,-1.8748214f,-1.8829663f,-1.8950636f,-1.9199765f,-1.9326725f,-1.9016511f,-1.9020103f,-1.8914702f,-1.8606884f,-1.8294275f,-1.8258343f,-1.8019993f,-1.7807993f,-1.7693008f,-1.7483404f,-1.7552874f,-1.7457054f,-1.7513349f,-1.75912f,-1.7579224f,-1.7574434f,-1.7809188f,-1.7895428f,-1.7982862f,-1.8028376f,-1.826433f,-1.8439201f,-1.894824f,-1.9218928f,-1.9466858f,-1.9542319f,-1.9422544f,-1.9034477f,-1.8918297f,-1.8788941f,-1.8509868f,-1.825834f,-1.7720554f,-1.7690613f,-1.7742115f,-1.7726543f,-1.7881054f,-1.7937348f,-1.809665f,-1.8150547f,-1.8214025f,-1.8259541f,-1.826553f,-1.8326614f,-1.869791f,-1.8779358f,-1.8890747f,-1.9241686f,-1.9602207f,-1.9892056f,-1.9892057f,-1.9864509f,-1.9525551f,-1.9046452f,-1.8675156f,-1.8511064f,-1.7897822f,-1.7649889f,-1.7536104f,-1.7417526f,-1.742232f,-1.7373213f,-1.7374411f};
        float[] y = {0.18936259f,0.11426433f,0.14744172f,0.15702361f,0.15870048f,0.12636152f,0.0703073f,0.08108697f,0.06108471f,0.03928586f,0.0098214755f,0.007785318f,0.009821476f,0.005868937f,0.018684745f,0.03066213f,0.031859882f,0.04024405f,0.059048563f,0.07210391f,0.122049645f,0.13043383f,0.12755924f,0.11198863f,0.09066887f,0.10108922f,0.09402255f,0.06623498f,0.04455592f,-0.005868907f,-0.011378506f,-0.0016768221f,-0.010899412f,-0.003233881f,0.0011977523f,0.008743509f,0.031500556f,0.035572875f,0.037848573f,0.076295994f,0.11222817f,0.12983496f,0.11426433f,0.09390275f,0.077972844f,0.070067756f,0.07749374f,0.048748005f,0.016768364f,0.003952554f,0.001557076f,0.020122033f,0.010300568f,-0.012216923f,0.00011978708f,0.022637283f,0.03281807f,0.056892633f,0.071744606f,0.11426433f,0.11055134f,0.11941461f,0.12085191f,0.111749075f,0.11905529f,0.12169032f,0.10875473f,0.08983046f,0.05593444f,0.043837268f,0.05078416f,0.03102146f,0.027548015f,0.013414693f,0.03940564f,0.055335574f,0.050424833f,0.05892878f,0.06359996f,0.07294233f,0.11773779f,0.14217165f,0.1428903f,0.13390727f,0.12336717f,0.125044f,0.12899652f,0.13678184f,0.122528754f,0.09773553f,0.06731296f,0.063599974f,0.06336042f,0.051742345f,0.04108247f,0.047670037f,0.057731044f,0.05940789f,0.06443838f,0.076056466f,0.113425925f,0.13342817f,0.15366995f,0.1443276f,0.1402553f,0.148879f,0.1576225f,0.16528802f,0.15163381f,0.120971665f,0.08755475f,0.07689488f,0.07114573f,0.054497145f,0.03928586f,0.015450853f,0.024912989f,0.026829373f,0.025032762f,0.028745752f,0.010420344f,0.028146887f,0.04012428f,0.041920885f,0.04910732f,0.05246099f,0.054736704f,0.094860956f,0.11186887f,0.114384115f,0.09510051f,0.08000898f,0.06767228f,0.08108696f,0.07593668f,0.048029356f,0.016289266f,-0.013414663f,-0.028985271f,-0.03221917f,-0.033416912f,-0.037249677f,-0.03940561f,-0.025990928f,-0.02096042f,-0.014732179f,-0.02323613f,0.01760678f,0.051023703f,0.06503726f,0.076056466f,0.0680316f,0.07198414f,0.08623723f,0.10719769f,0.12061236f,0.10540106f,0.08491971f,0.08036831f,0.08192538f,0.079050794f,0.06551635f,0.050424837f,0.02000226f,0.004551424f,0.006348028f,0.009701705f,0.0043118773f,0.023835024f,0.009222608f,0.014013566f,0.051502805f,0.054976247f,0.063120864f,0.06036607f,0.071624815f,0.087914065f,0.11785755f,0.14025527f,0.13067338f,0.11414456f,0.103484675f,0.108036086f,0.13294908f,0.12276828f,0.10420333f,0.079769455f,0.06276156f,0.061204482f,0.07497849f,0.06455816f,0.051622577f,0.03126101f,0.0404836f,0.05222144f,0.043238405f,0.05761128f,0.050065517f,0.07354121f,0.106119715f,0.10959315f,0.122888066f,0.12528354f,0.12516376f,0.15462816f,0.16624624f,0.14959766f,0.14923832f,0.122648515f,0.1208519f,0.11234796f,0.07162482f,0.035213545f,0.0041921f,-0.017007884f,-0.017007884f,-0.036650807f,-0.03581239f,-0.03150053f,-0.04048357f,-0.037129905f,-0.039285835f,-0.030063242f,-0.011737831f};
        float[] z = {9.712749f,9.702329f,9.668911f,9.71742f,9.7022085f,9.666037f,9.672024f,9.647111f,9.668673f,9.662323f,9.658729f,9.710234f,9.672743f,9.692027f,9.693344f,9.7061615f,9.6995735f,9.739818f,9.712869f,9.695979f,9.738142f,9.713468f,9.700412f,9.727361f,9.69634f,9.6837635f,9.673582f,9.655376f,9.660646f,9.678732f,9.661006f,9.684122f,9.653581f,9.706999f,9.720653f,9.688914f,9.707239f,9.720414f,9.72185f,9.723409f,9.707239f,9.716223f,9.725085f,9.739578f,9.687116f,9.705802f,9.670347f,9.686399f,9.692865f,9.674898f,9.714305f,9.698135f,9.676217f,9.71203f,9.702208f,9.72293f,9.760776f,9.691788f,9.717299f,9.738979f,9.699095f,9.724486f,9.726044f,9.745207f,9.71766f,9.704724f,9.678493f,9.683165f,9.664598f,9.681967f,9.702327f,9.67993f,9.678133f,9.698734f,9.706041f,9.711551f,9.697897f,9.718977f,9.70664f,9.718735f,9.716822f,9.718977f,9.699453f,9.711791f,9.7186165f,9.686517f,9.706281f,9.684002f,9.663641f,9.662683f,9.677294f,9.67538f,9.665677f,9.666037f,9.697775f,9.685439f,9.677295f,9.689032f,9.702089f,9.714307f,9.70149f,9.706041f,9.70652f,9.6982565f,9.704126f,9.695501f,9.688913f,9.676695f,9.672625f,9.678133f,9.666755f,9.6679535f,9.657533f,9.672744f,9.683883f,9.68508f,9.676817f,9.693226f,9.704723f,9.692266f,9.702327f,9.695022f,9.708556f,9.69598f,9.710712f,9.709036f,9.716821f,9.7041235f,9.715742f,9.688435f,9.698974f,9.707478f,9.698614f,9.6995735f,9.686638f,9.670588f,9.673463f,9.672625f,9.670469f,9.680769f,9.679811f,9.653339f,9.700171f,9.707118f,9.698137f,9.698377f,9.718737f,9.706999f,9.712748f,9.698496f,9.717421f,9.708797f,9.712031f,9.70508f,9.715623f,9.712029f,9.704965f,9.693465f,9.693345f,9.688074f,9.677535f,9.670947f,9.68556f,9.684841f,9.693945f,9.694063f,9.705442f,9.686757f,9.694544f,9.705442f,9.707358f,9.724008f,9.716104f,9.7173f,9.725804f,9.72748f,9.709754f,9.714426f,9.708676f,9.708676f,9.721252f,9.719097f,9.699933f,9.70664f,9.675019f,9.676936f,9.666157f,9.68448f,9.672503f,9.686637f,9.678733f,9.690591f,9.699213f,9.675977f,9.713227f,9.698496f,9.687355f,9.711191f,9.707357f,9.718617f,9.690591f,9.701132f,9.699453f,9.727361f,9.684362f,9.697417f,9.682207f,9.714425f,9.694184f,9.676577f,9.663521f,9.666874f,9.662085f,9.664358f,9.683523f,9.684722f,9.695021f,9.660525f,9.7219715f,9.699934f,9.708677f,9.723409f,9.712389f,9.729277f};

        values = new ArrayList<>();
        for(int i = 0; i < x.length; ++i) { values.add(new float[]{x[i], y[i], z[i]}); }
    }

    @Test
    public void testMainMethod() {

        setUp();

        new RespiratoryRateMonitor().calculateRespiratoryRate(values);
    }
}