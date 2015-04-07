#Geode Network Configuration Best Practices





	Network architectural goals
	Network Interface Card (NIC) selection and configuration
	Switch configuration considerations
	General network infrastructure considerations
	TCP vs. UDP protocol considerations
	Socket communications and socket buffer settings
	TCP settings for congestion control, window scaling, etc.


	Architects: who can use this paper to inform key decisions and design choices surrounding a Geode solution
	System Engineers and Administrators: who can use this paper as a guide for system configuration

 


	Reduced risk analysis time from 6 hours to 20 minutes, allowing for record profits in the flash crash of 2008 that other firms were not able to monetize.
	Improved end-user response time from 3 seconds to 50 ms, worth 8 figures a year in new revenue from a project delivered in fewer than 6 months.
	Tracked assets in real time to coordinate all the right persons and machinery into the right place at the right time to take advantage of immediate high-value opportunities.
	Created end-user reservation systems that handle over a billion requests daily with no downtime.





	**UDP/IP Multicast** New members broadcast their connection information over the multicast address and port to all running members. Existing members respond to establish communication with the new member. 
	**Geode Locators Using TCP/IP** Peer locators manage a dynamic list of distributed system members. New members connect to one of the locators to retrieve the member list, which it uses to join the system.





****link to network1.png



	Propagation delays – these are the result of the distance that must be covered in order for data moving across the network to reach its destination and the medium through which the signal travels. This can range from a few nanoseconds or microseconds in local area networks (LANs) up to about 0.25 seconds in geostationary-satellite communications systems. 
	Transmission delays – these delays are the result of the time required to push all the packet’s bits into the link, which is a function of the packet’s length and the data rate of the link. For example, to transmit a 10 Mb file over a 1 Mbps link would require 10 seconds while the same transmission over a 100 Mbps link would take only 0.1 seconds. 
	Processing delays – these delays are the result of the time it takes to process the packet header, check for bit-level errors and determine the packet’s destination. Processing delays in high-speed routers are often minimal. However, for networks performing complex encryption or Deep Packet Inspection (DPI), processing delays can be quite large. In addition, routers performing Network Address Translation (NAT) also have higher than normal processing delays because those routers need to examine and modify both incoming and outgoing packets. 
	Queuing delays – these delays are the result of time spent by packets in routing queues. The practical reality of network design is that some queuing delays will occur.  Effective queue management techniques are critical to ensuring that the high-priority traffic experiences smaller delays while lower priority packets see longer delays. 


	Keep Geode members and clients on the same LAN
	Use network traffic encryption prudently
	Use the fastest link possible



	Protocol inefficiency – TCP is an adaptive protocol that seeks to balance the demands placed on network resources from all network peers while making efficient use of the underlying network infrastructure. TCP detects and responds to current network conditions using a variety of feedback mechanisms and algorithms. The mechanisms and algorithms have evolved over the years but the core principles remain the same:
+	All TCP connections begin with a three-way handshake that introduces latency and makes TCP connection creation expensive
+	TCP slow-start is applied to every new connection by default. This means that connections can’t immediately use the full capacity of the link. The time required to reach a specific throughput target is a function of both the round trip time between the client and server and the initial congestion window size.
+	TCP flow control and congestion control regulate the throughput of all TCP connections.
+	TCP throughput is regulated by the current congestion window size.
	Congestion – this occurs when a link or node is loaded to the point that its quality of service degrades. Typical effects include queuing delay, packet loss or blocking of new connections. As a result, an incremental increase in offered load on a congested network may result in an actual reduction in network throughput.  In extreme cases, networks may experience a congestion collapse where reduced throughput continues well after the congestion-inducing load has been eliminated and renders the network unusable.  This condition was first documented by John Nagle in 1984 and by 1986 had become a reality for the Department of Defense’s ARPANET – the precursor to the modern Internet and the world’s first operational packet-switched network. These incidents saw sustained reductions in capacity, in some cases capacity dropped by a factor of 1,000! Modern networks use flow control, congestion control and congestion avoidance techniques to avoid congestion collapse. These techniques include: exponential backoff, TCP Window reduction and fair queuing in devices like routers. Packet prioritization is another method used to minimize the effects of congestion. 



Best practices for achieving this goal include:
	Increasing TCP’s Initial Congestion Window
	Disabling TCP Slow-Start After Idle 
	Enabling Window Scaling (RFC 1323) 
	Enabling TCP Low Latency 
	Enabling TCP Fast Open 


***link to network3.png

	Access layer redundancy – The access layer is the first point of entry into the network for edge devices and end stations such as Geode servers. For Geode systems, this network layer should have attributes that support high availability including:
+	Operating system high-availability features, such as Link Aggregation (EtherChannel or 802.3ad), which provide higher effective bandwidth and resilience while reducing complexity.
+	Default gateway redundancy using dual connections to redundant systems (distribution layer switches) that use Gateway Load Balancing Protocol (GLBP), Hot Standby Router Protocol (HSRP), or Virtual Router Redundancy Protocol (VRRP). This provides fast failover from one switch to the backup switch at the distribution layer.
+	Switch redundancy using some form of Split Multi-Link Trunking (SMLT). The use of SMLT not only allows traffic to be load-balanced across all the links in an aggregation group but also allows traffic to be redistributed very quickly in the event of link or switch failure. In general the failure of any one component results in a traffic disruption lasting less than half a second (normal less than 100 milliseconds).
	Distribution layer redundancy – The distribution layer aggregates access layer nodes and creates a fault boundary providing a logical isolation point in the event of a failure in the access layer. High availability for this layer comes from dual equal-cost paths from the distribution layer to the core and from the access layer to the distribution layer. This network layer is usually designed for high availability and doesn’t typically require changes for Geode systems.
	Core layer redundancy – The core layer serves as the backbone for the network. The core needs to be fast and extremely resilient because everything depends on it for connectivity. This network layer is typically built as a high-speed, Layer 3 switching environment using only hardware-accelerated services and redundant point-to-point Layer 3 interconnections in the core. This layer is designed for high availability and doesn’t typically require changes for Geode systems.

	Use Mode 6 Network Interface Card (NIC) Bonding – NIC bonding involves combining multiple network connections in parallel in order to increase throughput and provide redundancy should one of the links fail. Linux supports six modes of link aggregation:
+	Mode 1 (active-backup) in this mode only one slave in the bond is active. A different slave becomes active if and only if the active slave fails. 
+	Mode 2 (balance-xor) in this mode a slave is selected to transmit based on a simple XOR calculation that determines which slave to use. This mode provides both load balancing and fault tolerance.
+	Mode 3 (broadcast) this mode transmits everything on all slave interfaces. This mode provides fault tolerance.
+	Mode 4 (IEEE 802.3ad) this mode creates aggregation groups that share the same speed and duplex settings and utilizes all slaves in the active aggregator according to the 802.3ad specification. 
+	Mode 5 (balance-tlb) this mode distributes outgoing traffic according to the load on each slave. One slave receives incoming traffic. If that slave fails, another slave takes over the MAC address of the failed receiving slave.
+	Mode 6 (balance-alb) this mode includes balance-tlb plus receive load balancing (rlb) for IPV4 traffic, and does not require any special switch support. The receive load balancing is achieved by ARP negotiation. The bonding driver intercepts the ARP Replies sent by the local system on their way out and overwrites the source hardware address with the unique hardware address of one of the slaves in the bond such that different peers use different hardware addresses for the server.

	Use SMLT for switch redundancy – the Split Multi-link Trunking (SMLT) protocol allows multiple Ethernet links to be split across multiple switches in a stack, preventing any single point of failure, and allowing switches to be load balanced across multiple aggregation switches from the single access stack. SMLT provides enhanced resiliency with sub-second failover and sub-second recovery for all speed trunks while operating transparently to end-devices. This allows for the creation of Active load sharing high availability network designs that meet five nines availability requirements. 





	Partitioned Data
	Smaller Distributed Systems
	Unpredictable Network Loads


+ 64K byte message size limit (including overhead for message headers)
 Markedly slower performance on congested networks 
 Limited reliability (Geode compensates through retransmission protocols)

	Replicated Data

	Larger Distributed Systems

	Socket Buffer Size
+	Peer-to-peer. The socket-buffer-size setting in gemfire.properties should be the same throughout the distributed system.
+	Client/server. The client’s pool socket-buffer size-should match the setting for the servers that the pool uses.
+	Server. The server socket-buffer size in the server’s cache configuration (e.g. cache.xml file) should match the values defined for the server’s clients. 
+	Multisite (WAN). If the link between sites isn’t optimized for throughput, it can cause messages to back up in the queues. If a receiving queue buffer overflows, it will get out of sync with the sender and the receiver won’t know it. A gateway sender's socket-buffer-size should match the gateway receiver’s socket-buffer-size for all receivers that the sender connects to.
	TCP/IP Keep Alive 


	TCP/IP Peer-to-Peer Handshake Timeouts


<table>
<tr>
<td>Setting</td>
<td>Recommended Value</td>
<td>Rationale</td>
</tr>
<tr>
<td>30000</td>
<td>Set maximum number of packets, queued on the INPUT side, when the interface receives packets faster than kernel can process them. Recommended setting is for 10GbE links. For 1GbE links use 8000.</td>
</tr>
<tr>
<td>67108864</td>
<td>Set max to 16MB (16777216) for 1GbE links and 64MB (67108864) for 10GbE links.</td>
</tr>
<tr>
<td>net.core.rmem_max</td>
<td>67108864</td>
<td>Set max to 16MB (16777216) for 1GbE links and 64MB (67108864) for 10GbE links.</td>
</tr>
<tr> 
<td>htcp</td>
<td>There seem to be bugs in both bic and cubic (the default) for a number of versions of the Linux  kernel up to version 2.6.33. The kernel version for Redhat 5.x is 2.6.18-x and 2.6.32-x for Redhat 6.x
</td>
</tr>
<tr>
<td>net.ipv4.tcp_congestion_window</td>
<td>10</td>
<td>This is the default for Linux operating systems based on Linux kernel 2.6.39 or later.</td>
</tr>
<tr>
<td>10</td>
<td>This setting determines the time that must elapse before TCP/IP can release a closed connection and reuse its resources. During this TIME_WAIT state, reopening the connection to the client costs less than establishing a new connection. By reducing the value of this entry, TCP/IP can release closed connections faster, making more resources available for new connections. The default value is 60. The recommened setting lowers its to 10. You can lower this even further, but too low, and you can run into socket close errors in networks with lots of jitter.</td>
</tr>
<tr>
<td>net.ipv4.tcp_keepalive_interval</td>
<td>30</td>
<td>This determines the wait time between isAlive interval probes. Default value is 75. Recommended value reduces this in keeping with the reduction of the overall keepalive time.</td>
</tr>
<tr>
<td>5</td>
<td>How many keepalive probes to send out before the socket is timed out. Default value is 9. Recommended value reduces this to 5 so that retry attempts will take 2.5 minutes.</td>
</tr>
<tr>
<td>600</td>	
<td>Set the TCP Socket timeout value to 10 minutes instead of 2 hour default. With an idle socket, the system will wait tcp_keepalive_time seconds, and after that try tcp_keepalive_probes times to send a TCP KEEPALIVE in intervals of tcp_keepalive_intvl seconds. If the retry attempts fail, the socket times out.</td>
</tr>
<tr>
<td>net.ipv4.tcp_low_latency</td>
<td>1</td>
<td>Configure TCP for low latency, favoring low latency over throughput</td>
</tr>
<tr>
<td>net.ipv4.tcp_max_orphans</td>
<td>16384</td>
<td>Limit number of orphans, each orphan can eat up to 16M (max wmem) of unswappable memory</td>
</tr>
<tr>
<td>net.ipv4.tcp_max_tw_buckets</td>
<td>1440000</td>
<td>Maximal number of timewait sockets held by system simultaneously. If this number is exceeded time-wait socket is immediately destroyed and warning is printed. This limit exists to help prevent simple DoS attacks.</td>
</tr>
<tr>
<td>net.ipv4.tcp_no_metrics_save</td>
<td>1</td>
<td>Disable caching TCP metrics on connection close</td>
</tr>
<tr>
<td>net.ipv4.tcp_orphan_retries</td>
<td>0</td>
<td>Limit number of orphans, each orphan can eat up to 16M (max wmem) of unswappable memory</td>
</tr>
<tr>
<td>net.ipv4.tcp_rfc1337</td>
<td>1</td>
<td>Enable a fix for RFC1337 - time-wait assassination hazards in TCP</td>
</tr>
<tr>
<td>net.ipv4.tcp_rmem</td>
<td>10240 131072 33554432</td>
<td>Setting is min/default/max. Recommed increasing the Linux autotuning TCP buffer limit to 32MB</td>
</tr>
<tr>
<td> net.ipv4.tcp_wmem</td>
<td>10240 131072 33554432</td>
<td>Setting is min/default/max. Recommed increasing the Linux autotuning TCP buffer limit to 32MB</td>
</tr>
<tr>
<td>net.ipv4.tcp_sack</td>
<td>1</td>
<td>Enable select acknowledgments</td>
</tr>
<tr>
<td>net.ipv4.tcp_slow_start_after_idle</td>
<td>0</td>
<td>By default, TCP starts with a single small segment, gradually increasing it by one each time. This results in unnecessary slowness that impacts the start of every request.</td>
</tr>
<tr>
<td>net.ipv4.tcp_syncookies</td>
<td>0</td>	
<td>Many default Linux installations use SYN cookies to protect the system against malicious attacks that flood TCP SYN packets. The use of SYN cookies dramatically reduces network bandwidth, and can be triggered by a running Geode cluster.
</td>
</tr>
<tr>
<td>1</td>
<td>Enable timestamps as defined in RFC1323:</td>
</tr>
<tr>
<td>net.ipv4.tcp_tw_recycle</td>
<td>1</td>
<td>This enables fast recycling of TIME_WAIT sockets. The default value is 0 (disabled). Should be used with caution with load balancers.</td>
</tr>
<tr>
<td>net.ipv4.tcp_tw_reuse</td>
<td>1</td>
<td>This allows reusing sockets in TIME_WAIT state for new connections when it is safe from protocol viewpoint. Default value is 0 (disabled). It is generally a safer alternative to tcp_tw_recycle. The tcp_tw_reuse setting is particularly useful in environments where numerous short connections are open and left in TIME_WAIT state, such as web servers and loadbalancers.</td>
</tr>
<tr>
<td>net.ipv4.tcp_window_scaling</td>
<td>1</td>	
<td>Turn on window scaling which can be an option to enlarge the transfer window:</td>
</tr>
</table>