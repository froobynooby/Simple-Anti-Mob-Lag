package com.froobworld.saml.group;

public class GroupMetadata {
    private final boolean vol;
    private final boolean restrictsEligibility;
    private final boolean restrictsMemberStatus;
    private final boolean restrictsGroupStatus;

    private GroupMetadata(boolean vol, boolean restrictsEligibility, boolean restrictsMemberStatus, boolean restrictsGroupStatus) {
        this.vol = vol;
        this.restrictsEligibility = restrictsEligibility;
        this.restrictsMemberStatus = restrictsMemberStatus;
        this.restrictsGroupStatus = restrictsGroupStatus;
    }


    public boolean isVolatile() {
        return vol;
    }

    public boolean restrictsEligibility() {
        return restrictsEligibility;
    }

    public boolean restrictsMemberStatus() {
        return restrictsMemberStatus;
    }

    public boolean restrictsGroupStatus() {
        return restrictsGroupStatus;
    }

    public static class Builder {
        private boolean vol;
        private boolean restrictsEligibility;
        private boolean restrictsMemberStatus;
        private boolean restrictsGroupStatus;

        public Builder() {
            this.vol = true;
            this.restrictsEligibility = true;
            this.restrictsMemberStatus = true;
            this.restrictsGroupStatus = true;
        }


        public Builder setVolatile(boolean vol) {
            this.vol = vol;
            return this;
        }

        public Builder setRestrictsEligibility(boolean restrictsEligibility) {
            this.restrictsEligibility = restrictsEligibility;
            return this;
        }

        public Builder setRestrictsMemberStatus(boolean restrictsMemberStatus) {
            this.restrictsMemberStatus = restrictsMemberStatus;
            return this;
        }

        public Builder setRestrictsGroupStatus(boolean restrictsGroupStatus) {
            this.restrictsGroupStatus = restrictsGroupStatus;
            return this;
        }

        public GroupMetadata build() {
            return new GroupMetadata(vol, restrictsEligibility, restrictsMemberStatus, restrictsGroupStatus);
        }

    }

}
