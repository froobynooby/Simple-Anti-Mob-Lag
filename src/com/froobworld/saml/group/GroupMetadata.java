package com.froobworld.saml.group;

public class GroupMetadata {
    private boolean vol;
    private boolean restrictsMembers;
    private boolean restrictsGroupStatus;

    private GroupMetadata(boolean vol, boolean restrictsMembers, boolean restrictsGroupStatus) {
        this.vol = vol;
        this.restrictsMembers = restrictsMembers;
        this.restrictsGroupStatus = restrictsGroupStatus;
    }


    public boolean isVolatile() {
        return vol;
    }

    public boolean restrictsMembers() {
        return restrictsMembers;
    }

    public boolean restrictsGroupStatus() {
        return restrictsGroupStatus;
    }

    public static class Builder {
        private boolean vol;
        private boolean restrictsMembers;
        private boolean restrictsGroupStatus;

        public Builder() {
            this.vol = true;
            this.restrictsMembers = true;
            this.restrictsGroupStatus = true;
        }


        public Builder setVolatile(boolean vol) {
            this.vol = vol;
            return this;
        }

        public Builder setRestrictsMembers(boolean restrictsMembers) {
            this.restrictsMembers = restrictsMembers;
            return this;
        }

        public Builder setRestrictsGroupStatus(boolean restrictsGroupStatus) {
            this.restrictsGroupStatus = restrictsGroupStatus;
            return this;
        }

        public GroupMetadata build() {
            return new GroupMetadata(vol, restrictsMembers, restrictsGroupStatus);
        }

    }

}
