package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class Estimator {

	private static final double ERR = 1e-5;
	private final double gk, k, vk, g, v, x;
	private final int max;
	private final Vec3 dp, ev;

	public Estimator(double G, double K, Vec3 pos, double V, int maxt, Vec3 ep, Vec3 eV) {
		gk = G / K;
		g = G;
		k = K;
		vk = V / K;
		v = V;
		max = maxt;
		ev = eV;
		Vec3 vdp = ep.subtract(pos);
		dp = vdp.add(Math.abs(vdp.x) < ERR ? ERR : 0, 0, Math.abs(vdp.z) < ERR ? ERR : 0);
		x = dis(dp.x, dp.z);
	}

	public static SolResult solve(Function<Double, Double> f, double v, double min, double max, double err) {
		double x0 = min;
		double x1 = max;
		double v0 = f.apply(x0);
		double v1 = f.apply(x1);
		if (Math.abs(v0 - v) < err)
			return new SucSolRes(x0);
		if (Math.abs(v1 - v) < err)
			return new SucSolRes(x1);
		if (v0 > v && v1 > v)
			return SolType.OVER;
		if (v0 < v && v1 < v)
			return SolType.BELOW;
		boolean inc = v0 < v1;
		while (Math.abs(x1 - x0) > err) {
			double x = (x0 + x1) / 2;
			double vm = f.apply(x);
			if (Math.abs(vm - v) < err)
				return new SucSolRes(x);
			if (v < vm && inc || v > vm && !inc)
				x1 = x;
			else
				x0 = x;
		}
		return new SucSolRes((x0 + x1) / 2);
	}

	public static SolResult solve(Function<Double, Double> f2, Function<Double, Double> f1, double v, double x0,
								  double x1, double err) {
		SolResult sr = solve(f1, 0, x0, x1, err);
		if (sr.getType() == SolType.ZERO) {
			double tip = sr.getVal();
			double max = f2.apply(tip);
			double v0 = f2.apply(x0);
			double v1 = f2.apply(x1);
			if (v > max && max > v0 && max > v1)
				return SolType.OVER;
			if (v < max && max < v0 && max < v1)
				return SolType.BELOW;
			if ((v - v0) * (v - max) < 0)
				return solve(f2, v, x0, tip, err);
			return solve(f2, v, tip, x1, err);
		}
		return solve(f2, v, x0, x1, err);
	}

	private static double dis(double a, double b) {
		return Math.sqrt(a * a + b * b);
	}

	public EstiResult getAnswer() {
		if (v * v - 2 * g * dp.y < 0)
			return EstiType.CLOSE;
		EstiResult ans = getIdeal();
		double[] data = new double[]{ans.getA(), ans.getT()};
		if (estimate(data, 0.1, 0.2, 3))
			return EstiType.FAIL;
		if (estimate(data, 0.01, 0.2, 0.3))
			return EstiType.FAIL;
		return new SucEstiRes(data[0], data[1], this);

	}

	public EstiResult getIdeal() {
		double xt0 = Math.abs((Math.sqrt(v * v - 2 * g * dp.y) + v) / g);
		double xt1 = Math.abs((-Math.sqrt(v * v - 2 * g * dp.y) + v) / g);
		double mint = Math.max(0, Math.min(xt0, xt1) + ERR);
		double maxt = Math.min(max, Math.max(xt0, xt1) - ERR);
		SolResult r0 = solve(this::get_0, 0, mint, maxt, ERR);
		SolResult r1;
		if (r0.getType() == SolType.ZERO) {
			double bp = r0.getVal();
			SolResult dr0 = solve(this::get_1, 0, mint, bp, ERR);
			SolResult dr1 = solve(this::get_1, 0, bp, maxt, ERR);
			if (dr0.getType() == SolType.ZERO)
				r1 = dr0;
			else
				r1 = dr1;
		} else
			r1 = solve(this::get_1, 0, mint, maxt, ERR);
		if (r1.getType() != SolType.ZERO)
			return EstiType.FAIL;
		double t0 = r1.getVal();
		double a0 = Math.asin(dp.y / v / t0 + g * t0 / 2 / v);
		return new SucEstiRes(a0, t0, this);
	}

	public double getX0(double a, double t) {
		double xt = dp.x + ev.x * t;
		double zt = dp.z + ev.z * t;
		return vk * Math.cos(a) * (1 - Math.exp(-k * t)) - Math.sqrt(xt * xt + zt * zt);
	}

	public double getY0(double a, double t) {
		return -gk * t + (vk * Math.sin(a) + gk / k) * (1 - Math.exp(-k * t)) - dp.y - ev.y * t;
	}

	private boolean estimate(double[] data, double DA, double DT, double ER) {
		double a0 = data[0];
		double t0 = data[1];
		double x0 = getX0(a0, t0);
		double y0 = getY0(a0, t0);
		double len;
		int count = 0;
		boolean out = false;
		while ((len = dis(x0, y0)) > ER) {
			count++;
			double da = x0 * getXA(a0, t0) + y0 * getYA(a0, t0);
			double dt = x0 * getXT(a0, t0) + y0 * getYT(a0, t0);
			if (da > 0)
				a0 -= DA;
			if (da < 0)
				a0 += DA;
			if (dt > 0)
				t0 -= DT;
			if (dt < 0)
				t0 += DT;
			if (out |= (a0 < -Math.PI / 2 || a0 > Math.PI / 2))
				break;
			if (out |= (t0 < 0 || t0 > max))
				break;
			x0 = getX0(a0, t0);
			y0 = getY0(a0, t0);
			if (Math.abs(dis(x0, y0) - len) < ERR)
				break;
			if (count > 100)
				break;
		}
		data[0] = Mth.clamp(a0, -Math.PI / 2, Math.PI / 2);
		data[1] = Mth.clamp(t0, 0, max);
		return out;
	}

	private double get_0(double t) {
		double mul = dp.y / v / t + g * t / 2 / v;
		double m = Math.sqrt(1 - mul * mul);
		return v * m - v * t * mul / m * (g / 2 / v - dp.y / v / t / t);
	}

	private double get_1(double t) {
		double mul = dp.y / v / t + g * t / 2 / v;
		return v * t * Math.sqrt(1 - mul * mul) - x;
	}

	private double getXA(double a, double t) {
		return -vk * Math.sin(a) * (1 - Math.exp(-k * t));
	}

	private double getXT(double a, double t) {
		double xt = dp.x + ev.x * t;
		double zt = dp.z + ev.z * t;
		double ext = ((ev.x * ev.x + ev.z * ev.z) * t + dp.x * ev.x + dp.z * ev.z) / Math.sqrt(xt * xt + zt * zt);
		return Math.cos(a) * v * Math.exp(-k * t) - ext;
	}

	private double getYA(double a, double t) {
		return vk * Math.cos(a) * (1 - Math.exp(-k * t));
	}

	private double getYT(double a, double t) {
		return (Math.sin(a) * v + gk) * Math.exp(-k * t) - gk - ev.y;
	}

	public enum EstiType implements EstiResult {
		ZERO, FAIL, CLOSE;

		@Override
		public double getA() {
			return 0;
		}

		@Override
		public double getT() {
			return 0;
		}

		@Override
		public EstiType getType() {
			return this;
		}

		@Override
		public Vec3 getVec() {
			return Vec3.ZERO;
		}
	}

	public enum SolType implements SolResult {
		OVER, BELOW, ZERO;

		@Override
		public SolType getType() {
			return this;
		}

		@Override
		public double getVal() {
			return 0;
		}
	}

	public interface EstiResult {

		double getA();

		double getT();

		EstiType getType();

		Vec3 getVec();

	}

	public interface SolResult {

		SolType getType();

		double getVal();

	}

	private static class SucEstiRes implements EstiResult {

		private final double a, t;
		private final Estimator mov;

		private SucEstiRes(double A, double T, Estimator m) {
			a = A;
			t = T;
			mov = m;
		}

		@Override
		public double getA() {
			return a;
		}

		@Override
		public double getT() {
			return t;
		}

		@Override
		public EstiType getType() {
			return EstiType.ZERO;
		}

		@Override
		public Vec3 getVec() {
			Vec3 fin = mov.dp.add(mov.ev.scale(t)).multiply(1, 0, 1);
			double l = fin.length();
			double c = Math.cos(a);
			return new Vec3(mov.v * c * fin.x / l, mov.v * Math.sin(a), mov.v * c * fin.z / l);
		}

	}

	private static class SucSolRes implements SolResult {

		private final double val;

		private SucSolRes(double value) {
			val = value;
		}

		@Override
		public SolType getType() {
			return SolType.ZERO;
		}

		@Override
		public double getVal() {
			return val;
		}

	}

}
