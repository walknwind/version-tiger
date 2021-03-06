package com.inventage.tools.versiontiger.internal.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.Version;

public class MavenVersionImpl implements MavenVersion {
	
	private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+(\\.\\d+){0,2})(-([\\w\\-\\_]+))?$");
	private static final String MAVEN_SUFFIX_DELIMITER = "-";
	private static final String MAVEN_SNAPSHOT_SUFFIX = "SNAPSHOT";
	
	private final VersionFactory versionFactory;
	
	private final GeneralVersion gv;
	private final String suffix;
	
	public MavenVersionImpl(String version, VersionFactory versionFactory) {
		this.versionFactory = versionFactory;
		Matcher matcher = VERSION_PATTERN.matcher(version);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid maven version: " + version);
		}
		
		String inputSuffix = matcher.group(4);
		
		if (inputSuffix != null && inputSuffix.toLowerCase().endsWith(MAVEN_SNAPSHOT_SUFFIX.toLowerCase())) {
			if (MAVEN_SNAPSHOT_SUFFIX.length() < inputSuffix.length()) {
				suffix = inputSuffix.substring(0, inputSuffix.indexOf(MAVEN_SNAPSHOT_SUFFIX) - 1);
			} else {
				suffix = null;
			}
			gv = new GeneralVersion(matcher.group(1), true);
		} else {
			suffix = inputSuffix;
			gv = new GeneralVersion(matcher.group(1), false);
		}
	}
	
	public MavenVersionImpl(Integer major, Integer minor, Integer bugfix, String suffix, boolean snapshot, VersionFactory versionFactory) {
		this.versionFactory = versionFactory;
		gv = new GeneralVersion(major, minor, bugfix, snapshot);
		this.suffix = suffix;
	}
	
	@Override
	public String suffix() {
		return suffix;
	}
	
	@Override
	public String toString() {
		StringBuilder version = new StringBuilder(gv.versionString());
		
		if (suffix != null && !suffix.isEmpty()) {
			version.append(MAVEN_SUFFIX_DELIMITER);
			version.append(suffix);
		}

		if (gv.isSnapshot()) {
			version.append(MAVEN_SUFFIX_DELIMITER);
			version.append(MAVEN_SNAPSHOT_SUFFIX);
		}

		return version.toString();
	}

	@Override
	public MavenVersion incrementMajorAndSnapshot() {
		GeneralVersion inc = gv.incrementMajorAndSnapshot();
		
		return versionFactory.createMavenVersion(inc.major(), inc.minor(), inc.bugfix(), suffix, inc.isSnapshot());
	}

	@Override
	public MavenVersion incrementMinorAndSnapshot() {
		GeneralVersion inc = gv.incrementMinorAndSnapshot();
		
		return versionFactory.createMavenVersion(inc.major(), inc.minor(), inc.bugfix(), suffix, inc.isSnapshot());
	}

	@Override
	public MavenVersion incrementBugfixAndSnapshot() {
		GeneralVersion inc = gv.incrementBugfixAndSnapshot();
		
		return versionFactory.createMavenVersion(inc.major(), inc.minor(), inc.bugfix(), suffix, inc.isSnapshot());
	}

	@Override
	public MavenVersion releaseVersion() {
		return gv.isSnapshot() ? versionFactory.createMavenVersion(gv.major(), gv.minor(), gv.bugfix(), suffix, false) : this;
	}

	@Override
	public MavenVersion snapshotVersion() {
		return gv.isSnapshot() ? this : versionFactory.createMavenVersion(gv.major(), gv.minor(), gv.bugfix(), suffix, true);
	}

	@Override
	public Integer major() {
		return gv.major();
	}
	
	@Override
	public Integer minor() {
		return gv.minor();
	}
	
	@Override
	public Integer bugfix() {
		return gv.bugfix();
	}
	
	@Override
	public boolean isSnapshot() {
		return gv.isSnapshot();
	}

	@Override
	public boolean isLowerThan(Version other, boolean inclusive) {
		return gv.isLowerThan(other, inclusive);
	}

	@Override
	public int compareTo(Version o) {
		return gv.compareTo(o);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + gv.hashCode();
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MavenVersionImpl other = (MavenVersionImpl) obj;
		if (!gv.equals(other.gv))
			return false;
		if (suffix == null) {
			if (other.suffix != null)
				return false;
		} else if (!suffix.equals(other.suffix))
			return false;
		return true;
	}
	
}
